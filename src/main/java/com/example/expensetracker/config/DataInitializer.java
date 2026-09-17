package com.example.expensetracker.config;

import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.service.ExpenseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ExpenseRepository expenseRepository;
    private final ExpenseService expenseService;

    public DataInitializer(ExpenseRepository expenseRepository, ExpenseService expenseService) {
        this.expenseRepository = expenseRepository;
        this.expenseService = expenseService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (expenseRepository.count() == 0) {
            System.out.println("--> Pre-loading initial sample expense data...");

            // Set default budget
            expenseService.setMonthlyBudget(new BigDecimal("35000.00"));

            LocalDate today = LocalDate.now();

            List<Expense> sampleExpenses = Arrays.asList(
                new Expense("Grocery Shopping at Supermarket", new BigDecimal("4250.00"), Category.FOOD, today.minusDays(1), "Weekly groceries and household items"),
                new Expense("Monthly Apartment Rent", new BigDecimal("14000.00"), Category.HOUSING, today.minusDays(5), "House rent for current month"),
                new Expense("Electricity & Broadband Bill", new BigDecimal("2450.00"), Category.UTILITIES, today.minusDays(3), "WIFI + Electricity bill payment"),
                new Expense("Fuel / Metro Pass", new BigDecimal("1800.00"), Category.TRANSPORTATION, today.minusDays(2), "Commute expenses"),
                new Expense("Weekend Dinner & Movies", new BigDecimal("1650.00"), Category.ENTERTAINMENT, today.minusDays(6), "Outing with friends"),
                new Expense("Online Java & Spring Boot Course", new BigDecimal("1200.00"), Category.EDUCATION, today.minusDays(10), "Skill upgrade course"),
                new Expense("Pharmacy & Health Checkup", new BigDecimal("850.00"), Category.HEALTHCARE, today.minusDays(8), "Medicines and consultation"),
                new Expense("New Clothes & Footwear", new BigDecimal("3200.00"), Category.SHOPPING, today.minusDays(12), "Festival shopping"),
                new Expense("SIP Investment Fund", new BigDecimal("5000.00"), Category.SAVINGS, today.minusDays(4), "Monthly mutual fund SIP"),
                
                // Past months data for trend chart
                new Expense("Previous Month Utilities", new BigDecimal("2100.00"), Category.UTILITIES, today.minusMonths(1).minusDays(5), "Past bill"),
                new Expense("Previous Month Groceries", new BigDecimal("3900.00"), Category.FOOD, today.minusMonths(1).minusDays(10), "Past grocery"),
                new Expense("Previous Month Rent", new BigDecimal("14000.00"), Category.HOUSING, today.minusMonths(1).minusDays(15), "Past rent"),
                new Expense("Old Tech Accessories", new BigDecimal("2500.00"), Category.SHOPPING, today.minusMonths(2).minusDays(12), "Headphones purchase"),
                new Expense("Old Rent Payment", new BigDecimal("14000.00"), Category.HOUSING, today.minusMonths(2).minusDays(15), "Past rent")
            );

            expenseRepository.saveAll(sampleExpenses);
            System.out.println("--> Sample data loaded successfully (" + sampleExpenses.size() + " expenses added).");
        }
    }
}
