package com.itq.saleService.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itq.saleService.dao.SaleDao;
import com.itq.saleService.dto.Sale;

@Service
public class SaleBusiness {
	 @Autowired
	 private SaleDao saleDao;
	 public boolean createSale(Sale sale) {
		 return saleDao.createSale(sale);
	 }
	 
	 public Sale getSaleById(int saleId) {
        return saleDao.getSaleById(saleId);
     }
     public List<Sale> getAllSales() {
	 	return (List<Sale>) saleDao.getAllSales();
	 }
     public List<Sale> getSalesByCategory(String category) {
	 	return (List<Sale>) saleDao.getSaleByCategory(category);
	 }
     public List<Sale> getSalesByClient(int client) {
 	 	return (List<Sale>) saleDao.getSaleByClient(client);
 	 }
     public List<Sale> getSalesByDate(String date) {
 	 	return (List<Sale>) saleDao.getSaleByDate(date);
 	 }
     public boolean updateSaleStatus(int userId, String newStatus) {
        return saleDao.updateSaleStatus(userId, newStatus);
     }

     public boolean deleteSale(int saleId) {
        return saleDao.deleteSale(saleId);
     }


}
