package com.example.springboot.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.example.springboot.domain.Cidade;
import com.example.springboot.domain.Cliente;
import com.example.springboot.domain.Endereco;
import com.example.springboot.domain.enums.TipoCliente;
import com.example.springboot.dto.ClienteDTO;
import com.example.springboot.dto.ClienteNewDTO;
import com.example.springboot.reposistories.ClienteRepository;
import com.example.springboot.reposistories.EnderecoRepository;
import com.example.springboot.services.exceptions.DataIntegrityException;
import com.example.springboot.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;
	@Autowired
	private EnderecoRepository enderecoRepository;

	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		for (Endereco end : obj.getEnderecos()) {
			enderecoRepository.saveAll(Arrays.asList(end));
			
		}
		return repo.save(obj);
	}

	public Cliente find(Integer id) {
		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + " - Tipo: " + Cliente.class.getName()));
	}

	public Cliente update(Cliente objDto) {
		Cliente objDb = find(objDto.getId());
		updateData(objDb, objDto);
		return repo.save(objDb);
	}

	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há entidades relacionadas!");
		}
	}

	public List<Cliente> findAll() {
		List<Cliente> list = repo.findAll();
		return list;
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String direction, String orderBy) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null);
	}

	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cliente = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(), TipoCliente.toEnum(objDto.getTipo()));
		Cidade cidade = new Cidade(objDto.getCidadeId(), null, null);
		Endereco endereco = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cliente, cidade);
		cliente.getEnderecos().add(endereco);
		cliente.getTelefones().add(objDto.getTelefone1());
		if (objDto.getTelefone2() != null) {
			cliente.getTelefones().add(objDto.getTelefone2());
		}
		if (objDto.getTelefone3() != null) {
			cliente.getTelefones().add(objDto.getTelefone3());
		}
		return cliente;
	}

	private void updateData(Cliente ojbDb, Cliente objDto) {
		ojbDb.setNome(objDto.getNome());
		ojbDb.setEmail(objDto.getEmail());
	}

}
