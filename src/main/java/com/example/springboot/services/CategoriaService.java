package com.example.springboot.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.domain.Categoria;
import com.example.springboot.reposistories.CategoriaRepository;
import com.example.springboot.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repo;

	public Categoria find(Integer id) {
		Optional<Categoria> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + " - Tipo: " + Categoria.class.getName()));
	}

	public Categoria insert(Categoria obj) {
		obj.setId(null);
		Categoria savedObj = repo.save(obj);
		return savedObj;
	}

	public Categoria update(Categoria obj) {
		find(obj.getId());
		Categoria savedObj = repo.save(obj);
		return savedObj;
	}

}
