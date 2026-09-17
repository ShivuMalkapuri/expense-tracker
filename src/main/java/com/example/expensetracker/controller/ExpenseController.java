package com.example.expensetracker.controller;

import com.example.expensetracker.model.Budget;
import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.ExpenseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Controller
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/")
    public String showDashboard(Model model) {
        // KPI Metrics
        BigDecimal totalSpent = expenseService.getTotalExpenses();
        BigDecimal monthSpent = expenseService.getCurrentMonthExpenses();
        Budget currentBudget = expenseService.getCurrentMonthBudget();
        BigDecimal remainingBudget = currentBudget.getMonthlyLimit().subtract(monthSpent);
        double budgetProgress = expenseService.getBudgetProgressPercentage();

        // Chart Data
        Map<Category, BigDecimal> categoryData = expenseService.getCategoryExpensesForCurrentMonth();
        List<String> categoryLabels = new ArrayList<>();
        List<BigDecimal> categoryValues = new ArrayList<>();
        List<String> categoryColors = new ArrayList<>();

        categoryData.forEach((cat, sum) -> {
            if (sum.compareTo(BigDecimal.ZERO) > 0) {
                categoryLabels.add(cat.getDisplayName());
                categoryValues.add(sum);
                categoryColors.add(cat.getColorHex());
            }
        });

        Map<String, BigDecimal> trendData = expenseService.getMonthlyTrendData();
        List<String> trendLabels = new ArrayList<>(trendData.keySet());
        List<BigDecimal> trendValues = new ArrayList<>(trendData.values());

        // Recent Expenses (limit 5)
        List<Expense> allExpenses = expenseService.getAllExpenses();
        List<Expense> recentExpenses = allExpenses.size() > 5 ? allExpenses.subList(0, 5) : allExpenses;

        // Model attributes for Thymeleaf
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("monthSpent", monthSpent);
        model.addAttribute("monthlyLimit", currentBudget.getMonthlyLimit());
        model.addAttribute("remainingBudget", remainingBudget);
        model.addAttribute("budgetProgress", budgetProgress);
        model.addAttribute("categories", Category.values());
        model.addAttribute("newExpense", new Expense());
        model.addAttribute("recentExpenses", recentExpenses);

        // JSON-ready arrays for Chart.js
        model.addAttribute("chartCategoryLabels", categoryLabels);
        model.addAttribute("chartCategoryValues", categoryValues);
        model.addAttribute("chartCategoryColors", categoryColors);
        model.addAttribute("chartTrendLabels", trendLabels);
        model.addAttribute("chartTrendValues", trendValues);

        return "dashboard";
    }

    @PostMapping("/")
    public String handleDashboardPost(Model model) {
        return showDashboard(model);
    }

    @GetMapping("/expenses")
    public String listExpenses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        List<Expense> expenses = expenseService.filterExpenses(keyword, category, startDate, endDate);
        BigDecimal totalFiltered = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Budget currentBudget = expenseService.getCurrentMonthBudget();

        model.addAttribute("expenses", expenses);
        model.addAttribute("categories", Category.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("totalFiltered", totalFiltered);
        model.addAttribute("monthlyLimit", currentBudget.getMonthlyLimit());
        model.addAttribute("newExpense", new Expense());

        return "expenses";
    }

    @GetMapping("/expenses/new")
    public String showAddExpenseForm(Model model) {
        model.addAttribute("categories", Category.values());
        model.addAttribute("todayDate", LocalDate.now());
        return "add-expense";
    }

    @GetMapping("/budget")
    public String showSetBudgetForm(Model model) {
        Budget currentBudget = expenseService.getCurrentMonthBudget();
        model.addAttribute("monthlyLimit", currentBudget.getMonthlyLimit());
        return "set-budget";
    }

    @PostMapping("/expenses/save")
    public String saveExpense(
            @RequestParam("title") String title,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("category") Category category,
            @RequestParam(value = "expenseDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expenseDate,
            @RequestParam(value = "notes", required = false) String notes,
            RedirectAttributes redirectAttributes) {

        if (expenseDate == null) {
            expenseDate = LocalDate.now();
        }

        Expense expense = new Expense(title, amount, category, expenseDate, notes);
        expenseService.saveExpense(expense);
        redirectAttributes.addFlashAttribute("successMessage", "Expense added successfully!");
        return "redirect:/";
    }

    @GetMapping("/expenses/delete/{id}")
    public String deleteExpense(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        expenseService.deleteExpense(id);
        redirectAttributes.addFlashAttribute("successMessage", "Expense deleted successfully!");
        return "redirect:/expenses";
    }

    @PostMapping("/budget/set")
    public String setBudget(
            @RequestParam("monthlyLimit") BigDecimal monthlyLimit,
            RedirectAttributes redirectAttributes) {
        if (monthlyLimit != null && monthlyLimit.compareTo(BigDecimal.ZERO) > 0) {
            expenseService.setMonthlyBudget(monthlyLimit);
            redirectAttributes.addFlashAttribute("successMessage", "Monthly budget updated successfully!");
        }
        return "redirect:/";
    }

    @GetMapping("/expenses/export")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Expense> expenses = expenseService.filterExpenses(keyword, category, startDate, endDate);
        String csvContent = expenseService.exportToCsv(expenses);
        byte[] bytes = csvContent.getBytes();

        String filename = "expenses_report_" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }
}
