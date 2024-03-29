package com.vinicius.dscommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.vinicius.dscommerce.DTOs.CategoryDTO;
import com.vinicius.dscommerce.DTOs.ProductDTO;
import com.vinicius.dscommerce.DTOs.ProductMinDTO;
import com.vinicius.dscommerce.entities.Category;
import com.vinicius.dscommerce.entities.Product;
import com.vinicius.dscommerce.projections.ProductProjection;
import com.vinicius.dscommerce.repositories.ProductRepository;
import com.vinicius.dscommerce.services.exceptions.DatabaseException;
import com.vinicius.dscommerce.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;



@Service
public class ProductService {
	
	@Autowired
	ProductRepository repository;
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		return new ProductDTO(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado")));
	}
	
	@Transactional(readOnly = true)
	public Page<ProductMinDTO> searchByProductName(String name, Pageable pageable) {
		try {
			Page<ProductProjection> list = repository.searchByProductName(name, pageable);
			Page<ProductMinDTO> dto = list.map(x -> new ProductMinDTO(x));
			return dto;
		} catch (Exception e) {
			throw new ResourceNotFoundException("Produto não encontrado");
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
			Product entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new ProductDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Recurso não encontrado!");
		}	
	}
	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setPrice(dto.getPrice());
		entity.setImgUrl(dto.getImgUrl());
		entity.getCategoryes().clear();
		for(CategoryDTO x : dto.getCategories()) {
			Category cat = new Category();
			cat.setId(x.getId());
			entity.getCategoryes().add(cat);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
		try {
	        repository.deleteById(id);    		
		}
	    catch (DataIntegrityViolationException e) {
	        throw new DatabaseException("Falha de integridade referencial");
	   	}
	}
}
