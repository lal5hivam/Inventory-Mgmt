package com.phegondev.InventoryMgtSystem.services.impl;


import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.dtos.TransactionDTO;
import com.phegondev.InventoryMgtSystem.dtos.TransactionRequest;
import com.phegondev.InventoryMgtSystem.enums.TransactionStatus;
import com.phegondev.InventoryMgtSystem.enums.TransactionType;
import com.phegondev.InventoryMgtSystem.exceptions.NameValueRequiredException;
import com.phegondev.InventoryMgtSystem.exceptions.NotFoundException;
import com.phegondev.InventoryMgtSystem.models.Product;
import com.phegondev.InventoryMgtSystem.models.Supplier;
import com.phegondev.InventoryMgtSystem.models.Transaction;
import com.phegondev.InventoryMgtSystem.models.User;
import com.phegondev.InventoryMgtSystem.repositories.ProductRepository;
import com.phegondev.InventoryMgtSystem.repositories.SupplierRepository;
import com.phegondev.InventoryMgtSystem.repositories.TransactionRepository;
import com.phegondev.InventoryMgtSystem.services.TransactionService;
import com.phegondev.InventoryMgtSystem.services.UserService;
import com.phegondev.InventoryMgtSystem.specification.TransactionFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

	@Autowired
    private  TransactionRepository transactionRepository;
	@Autowired
    private  ProductRepository productRepository;
	@Autowired
    private  SupplierRepository supplierRepository;
	@Autowired
    private  UserService userService;
	@Autowired
    private  ModelMapper modelMapper;

    @Override
    public Response purchase(TransactionRequest transactionRequest) {
        Long productId = transactionRequest.getProductId();
        Long supplierId = transactionRequest.getSupplierId();
        Integer quantity = transactionRequest.getQuantity();

        if (supplierId == null) throw new NameValueRequiredException("Supplier Id is Required");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("Supplier Not Found"));

        User user = userService.getCurrentLoggedInUser();

        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.PURCHASE);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setProduct(product);
        transaction.setUser(user);
        transaction.setSupplier(supplier);
        transaction.setTotalProducts(quantity);
        transaction.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setNote(transactionRequest.getNote());

        transactionRepository.save(transaction);

        Response response = new Response();
        response.setStatus(200);
        response.setMessage("Purchase Made successfully");
        return response;
    }

    @Override
    public Response sell(TransactionRequest transactionRequest) {
        Long productId = transactionRequest.getProductId();
        Integer quantity = transactionRequest.getQuantity();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        User user = userService.getCurrentLoggedInUser();

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.SALE);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setProduct(product);
        transaction.setUser(user);
        transaction.setTotalProducts(quantity);
        transaction.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setNote(transactionRequest.getNote());

        transactionRepository.save(transaction);

        Response response = new Response();
        response.setStatus(200);
        response.setMessage("Product Sale successfully made");
        return response;
    }

    @Override
    public Response returnToSupplier(TransactionRequest transactionRequest) {
        Long productId = transactionRequest.getProductId();
        Long supplierId = transactionRequest.getSupplierId();
        Integer quantity = transactionRequest.getQuantity();

        if (supplierId == null) throw new NameValueRequiredException("Supplier Id is Required");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("Supplier Not Found"));

        User user = userService.getCurrentLoggedInUser();

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.RETURN_TO_SUPPLIER);
        transaction.setStatus(TransactionStatus.PROCESSING);
        transaction.setProduct(product);
        transaction.setUser(user);
        transaction.setSupplier(supplier);
        transaction.setTotalProducts(quantity);
        transaction.setTotalPrice(BigDecimal.ZERO);
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setNote(transactionRequest.getNote());

        transactionRepository.save(transaction);

        Response response = new Response();
        response.setStatus(200);
        response.setMessage("Product Returned in progress");
        return response;
    }

    @Override
    public Response getAllTransactions(int page, int size, String filter) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Specification<Transaction> spec = TransactionFilter.byFilter(filter);
        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

        List<TransactionDTO> transactionDTOS = modelMapper.map(
                transactionPage.getContent(), new TypeToken<List<TransactionDTO>>() {}.getType()
        );

        transactionDTOS.forEach(dto -> {
            dto.setUser(null);
            dto.setProduct(null);
            dto.setSupplier(null);
        });

        Response response = new Response();
        response.setStatus(200);
        response.setMessage("success");
        response.setTransactions(transactionDTOS);
        response.setTotalElements(transactionPage.getTotalElements());
        response.setTotalPages(transactionPage.getTotalPages());
        return response;
    }

    @Override
    public Response getAllTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction Not Found"));

        TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);
        if (transactionDTO.getUser() != null) {
            transactionDTO.getUser().setTransactions(null);
        }

        Response response = new Response();
        response.setStatus(200);
        response.setMessage("success");
        response.setTransaction(transactionDTO);
        return response;
    }

    @Override
    public Response getAllTransactionByMonthAndYear(int month, int year) {
        List<Transaction> transactions = transactionRepository.findAll(
                TransactionFilter.byMonthAndYear(month, year)
        );

        List<TransactionDTO> transactionDTOS = modelMapper.map(
                transactions, new TypeToken<List<TransactionDTO>>() {}.getType()
        );

        transactionDTOS.forEach(dto -> {
            dto.setUser(null);
            dto.setProduct(null);
            dto.setSupplier(null);
        });

        Response response = new Response();
        response.setStatus(200);
        response.setMessage("success");
        response.setTransactions(transactionDTOS);
        return response;
    }

    @Override
    public Response updateTransactionStatus(Long transactionId, TransactionStatus status) {
        Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction Not Found"));

        existingTransaction.setStatus(status);
        existingTransaction.setUpdateAt(LocalDateTime.now());
        transactionRepository.save(existingTransaction);

        Response response = new Response();
        response.setStatus(200);
        response.setMessage("Transaction Status Successfully Updated");
        return response;
    }
}
