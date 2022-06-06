package com.example.springboot.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.example.springboot.domain.Categoria;
import com.example.springboot.dto.CategoriaDTO;
import com.example.springboot.reposistories.CategoriaRepository;
import com.example.springboot.services.exceptions.DataIntegrityException;
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

	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir uma categoria que possui produtos!");
		}
	}

	public List<Categoria> findAll() {
		List<Categoria> list = repo.findAll();
		return list;
	}

	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String direction, String orderBy) {
	  PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
	  return repo.findAll(pageRequest);
	}
	
	public Categoria fromDTO(CategoriaDTO dto) {
		return new Categoria(dto.getId(), dto.getNome());
	}

}
