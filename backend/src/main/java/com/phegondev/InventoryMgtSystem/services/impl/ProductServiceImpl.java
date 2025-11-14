package com.phegondev.InventoryMgtSystem.services.impl;


import com.phegondev.InventoryMgtSystem.dtos.ProductDTO;
import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.exceptions.NotFoundException;
import com.phegondev.InventoryMgtSystem.models.Category;
import com.phegondev.InventoryMgtSystem.models.Product;
import com.phegondev.InventoryMgtSystem.repositories.CategoryRepository;
import com.phegondev.InventoryMgtSystem.repositories.ProductRepository;
import com.phegondev.InventoryMgtSystem.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

	@Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    
    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    // Use relative paths that work on any OS
    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + File.separator + "product-images" + File.separator;
    private static final String IMAGE_DIRECTORY_2 = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "products" + File.separator;


    @Override
    public Response saveProduct(ProductDTO productDTO, MultipartFile imageFile) {
        try {
            log.info("Starting to save product: {}", productDTO.getName());
            
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category Not Found"));
            
            log.info("Category found: {}", category.getName());

            Product productToSave = new Product();
            productToSave.setName(productDTO.getName());
            productToSave.setSku(productDTO.getSku());
            productToSave.setPrice(productDTO.getPrice());
            productToSave.setStockQuantity(productDTO.getStockQuantity());
            productToSave.setDescription(productDTO.getDescription());
            productToSave.setCategory(category);

            if (imageFile != null && !imageFile.isEmpty()) {
                log.info("Processing image file: {}", imageFile.getOriginalFilename());
                String imagePath = saveImage2(imageFile);
                productToSave.setImageUrl(imagePath);
                log.info("Image saved at: {}", imagePath);
            }

            log.info("About to save product to database");
            Product savedProduct = productRepository.save(productToSave);
            log.info("Product saved successfully with ID: {}", savedProduct.getId());

            Response response = new Response();
            response.setStatus(200);
            response.setMessage("Product successfully saved");
            return response;
            
        } catch (Exception e) {
            log.error("Error saving product: ", e);
            throw e;
        }
    }
    @Override
    public Response updateProduct(ProductDTO productDTO, MultipartFile imageFile) {
        Product existingProduct = productRepository.findById(productDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage2(imageFile);
            existingProduct.setImageUrl(imagePath);
        }

        if (productDTO.getCategoryId() != null && productDTO.getCategoryId() > 0) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category Not Found"));
            existingProduct.setCategory(category);
        }

        if (productDTO.getName() != null && !productDTO.getName().isBlank()) {
            existingProduct.setName(productDTO.getName());
        }

        if (productDTO.getSku() != null && !productDTO.getSku().isBlank()) {
            existingProduct.setSku(productDTO.getSku());
        }

        if (productDTO.getDescription() != null && !productDTO.getDescription().isBlank()) {
            existingProduct.setDescription(productDTO.getDescription());
        }

        if (productDTO.getPrice() != null && productDTO.getPrice().compareTo(BigDecimal.ZERO) >= 0) {
            existingProduct.setPrice(productDTO.getPrice());
        }

        if (productDTO.getStockQuantity() != null && productDTO.getStockQuantity() >= 0) {
            existingProduct.setStockQuantity(productDTO.getStockQuantity());
        }

        productRepository.save(existingProduct);

        Response response = new Response();
        response.setStatus(200);
        response.setMessage("Product Updated successfully");
        return response;
    }

    @Override
    public Response getAllProducts() {
        List<Product> productList = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<ProductDTO> productDTOList = modelMapper.map(productList, new TypeToken<List<ProductDTO>>() {}.getType());

        Response response = new Response();
        response.setStatus(200);
        response.setMessage("success");
        response.setProducts(productDTOList);
        return response;
    }

    @Override
    public Response getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

        Response response = new Response();
        response.setStatus(200);
        response.setMessage("success");
        response.setProduct(productDTO);
        return response;
    }

    @Override
    public Response deleteProduct(Long id) {
        productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        productRepository.deleteById(id);

        Response response = new Response();
        response.setStatus(200);
        response.setMessage("Product Deleted successfully");
        return response;
    }

    @Override
    public Response searchProduct(String input) {
        List<Product> products = productRepository.findByNameContainingOrDescriptionContaining(input, input);

        if (products.isEmpty()) {
            throw new NotFoundException("Product Not Found");
        }

        List<ProductDTO> productDTOList = modelMapper.map(products, new TypeToken<List<ProductDTO>>() {}.getType());

        Response response = new Response();
        response.setStatus(200);
        response.setMessage("success");
        response.setProducts(productDTOList);
        return response;
    }

    private String saveImage(MultipartFile imageFile) {
        // Strict file validation
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        
        // Check file size (5MB max)
        long maxFileSize = 5 * 1024 * 1024; // 5MB
        if (imageFile.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size must not exceed 5MB");
        }
        
        // Validate content type
        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
        
        // Validate file extension
        String originalFilename = imageFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Invalid filename");
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!extension.matches("jpg|jpeg|png|webp|gif")) {
            throw new IllegalArgumentException("Only JPG, PNG, WEBP, and GIF images are allowed");
        }
        
        // Sanitize filename - remove path traversal attempts
        String sanitizedFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        File directory = new File(IMAGE_DIRECTORY);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IllegalArgumentException("Failed to create image directory");
            }
            log.info("Directory was created: {}", IMAGE_DIRECTORY);
        }

        String uniqueFileName = UUID.randomUUID() + "_" + sanitizedFilename;
        String imagePath = IMAGE_DIRECTORY + uniqueFileName;

        try {
            File destinationFile = new File(imagePath);
            // Verify the file is being saved in the correct directory (prevent path traversal)
            if (!destinationFile.getCanonicalPath().startsWith(new File(IMAGE_DIRECTORY).getCanonicalPath())) {
                throw new IllegalArgumentException("Invalid file path");
            }
            imageFile.transferTo(destinationFile);
            log.info("Image saved successfully: {}", uniqueFileName);
        } catch (Exception e) {
            log.error("Error saving image: {}", e.getMessage());
            throw new IllegalArgumentException("Error saving Image: " + e.getMessage());
        }
        return imagePath;
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    private String saveImage2(MultipartFile imageFile) {
        // Strict file validation
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        
        // Check file size (5MB max)
        long maxFileSize = 5 * 1024 * 1024; // 5MB
        if (imageFile.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size must not exceed 5MB");
        }
        
        // Validate content type
        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
        
        // Validate file extension
        String originalFilename = imageFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Invalid filename");
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!extension.matches("jpg|jpeg|png|webp|gif")) {
            throw new IllegalArgumentException("Only JPG, PNG, WEBP, and GIF images are allowed");
        }
        
        // Sanitize filename - remove path traversal attempts
        String sanitizedFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        File directory = new File(IMAGE_DIRECTORY_2);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info("Directory was created: {}", IMAGE_DIRECTORY_2);
            } else {
                log.error("Failed to create directory: {}", IMAGE_DIRECTORY_2);
                throw new IllegalArgumentException("Could not create image directory");
            }
        }

        String uniqueFileName = UUID.randomUUID() + "_" + sanitizedFilename;
        String imagePath = IMAGE_DIRECTORY_2 + uniqueFileName;

        try {
            File destinationFile = new File(imagePath);
            // Verify the file is being saved in the correct directory (prevent path traversal)
            if (!destinationFile.getCanonicalPath().startsWith(new File(IMAGE_DIRECTORY_2).getCanonicalPath())) {
                throw new IllegalArgumentException("Invalid file path");
            }
            imageFile.transferTo(destinationFile);
            log.info("Image saved successfully at: {}", imagePath);
        } catch (Exception e) {
            log.error("Error saving image: {}", e.getMessage());
            throw new IllegalArgumentException("Error saving Image: " + e.getMessage());
        }

        return "products/" + uniqueFileName;
    }
}
