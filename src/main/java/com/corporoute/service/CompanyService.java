package com.corporoute.service;

import com.corporoute.entity.Company;
import com.corporoute.exception.CompanyNotFoundException;
import com.corporoute.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException("Company not found"));
    }

    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company updateCompany(Long id, Company updatedCompany) {
        Company company = getCompanyById(id);

        company.setName(updatedCompany.getName());
        company.setCreditLimit(updatedCompany.getCreditLimit());
        company.setOutstandingBalance(updatedCompany.getOutstandingBalance());

        return companyRepository.save(company);
    }

    public void deleteCompany(Long id) {
        Company company = getCompanyById(id);
        companyRepository.delete(company);
    }
}