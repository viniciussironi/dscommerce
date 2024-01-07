package com.vinicius.dscommerce.entities.DTOs;

import com.vinicius.dscommerce.entities.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProductDTO {
	
	private Long id;
	
	@Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres")
	@NotBlank(message = "Campo requerido!")
	private String name;
	
	@Size(min = 10, message = "A descrição precisa ter pelo menos 10 caracteres")
	@NotBlank(message = "Campo requerido!")
	private String description;
	
	@Positive(message = "O preço deve ser positivo")
	private Double price;
	
	private String imgUrl;
	
	public ProductDTO() {
	}
	
	public ProductDTO(Product product) {
		id = product.getId();
		name = product.getName();
		description = product.getDescription();
		price = product.getPrice();
		imgUrl = product.getImgUrl();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Double getPrice() {
		return price;
	}

	public String getImgUrl() {
		return imgUrl;
	}
}