package com.example.expensetracker.service;

import com.example.expensetracker.model.Budget;
import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.BudgetRepository;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public ExpenseService(ExpenseRepository expenseRepository, BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAllByOrderByExpenseDateDescIdDesc();
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    @Transactional
    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Transactional
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public List<Expense> filterExpenses(String keyword, Category category, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.filterExpenses(keyword, category, startDate, endDate);
    }

    public BigDecimal getTotalExpenses() {
        BigDecimal total = expenseRepository.getTotalExpenseSum();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getCurrentMonthExpenses() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();
        BigDecimal total = expenseRepository.getExpenseSumBetween(start, end);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Budget getCurrentMonthBudget() {
        String currentMonthKey = YearMonth.now().toString(); // e.g. "2026-09"
        return budgetRepository.findByMonthYear(currentMonthKey)
                .orElseGet(() -> new Budget(new BigDecimal("30000.00"), currentMonthKey));
    }

    @Transactional
    public Budget setMonthlyBudget(BigDecimal amount) {
        String currentMonthKey = YearMonth.now().toString();
        Budget budget = budgetRepository.findByMonthYear(currentMonthKey)
                .orElse(new Budget(amount, currentMonthKey));
        budget.setMonthlyLimit(amount);
        return budgetRepository.save(budget);
    }

    public Map<Category, BigDecimal> getCategoryExpensesForCurrentMonth() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        List<Object[]> categorySums = expenseRepository.getCategorySumListBetween(start, end);
        Map<Category, BigDecimal> categoryMap = new EnumMap<>(Category.class);

        for (Category cat : Category.values()) {
            categoryMap.put(cat, BigDecimal.ZERO);
        }

        for (Object[] row : categorySums) {
            Category cat = (Category) row[0];
            BigDecimal sum = (BigDecimal) row[1];
            categoryMap.put(cat, sum);
        }

        return categoryMap;
    }

    public Map<String, BigDecimal> getMonthlyTrendData() {
        Map<String, BigDecimal> trendMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        // Last 6 months
        YearMonth current = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth targetMonth = current.minusMonths(i);
            LocalDate start = targetMonth.atDay(1);
            LocalDate end = targetMonth.atEndOfMonth();

            BigDecimal sum = expenseRepository.getExpenseSumBetween(start, end);
            String label = targetMonth.format(formatter);
            trendMap.put(label, sum != null ? sum : BigDecimal.ZERO);
        }
        return trendMap;
    }

    public double getBudgetProgressPercentage() {
        BigDecimal spent = getCurrentMonthExpenses();
        BigDecimal limit = getCurrentMonthBudget().getMonthlyLimit();

        if (limit.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }

        double percent = spent.divide(limit, 4, RoundingMode.HALF_UP).doubleValue() * 100;
        return Math.min(percent, 100.0);
    }

    public String exportToCsv(List<Expense> expenses) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Title,Amount,Category,Date,Notes\n");

        for (Expense e : expenses) {
            csv.append(e.getId()).append(",")
               .append("\"").append(e.getTitle().replace("\"", "\"\"")).append("\",")
               .append(e.getAmount()).append(",")
               .append(e.getCategory().getDisplayName()).append(",")
               .append(e.getExpenseDate()).append(",")
               .append("\"").append(e.getNotes() != null ? e.getNotes().replace("\"", "\"\"") : "").append("\"\n");
        }
        return csv.toString();
    }
}
