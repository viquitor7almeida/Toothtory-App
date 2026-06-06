package com.toothtory.services;

import com.toothtory.domain.entities.Consulta;
import com.toothtory.domain.entities.Paciente;
import com.toothtory.domain.entities.Procedimento;
import com.toothtory.domain.repositories.ConsultaRepository;
import com.toothtory.infra.repositories.ConsultaRepositoryImpl;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public class ConsultaService {
    private final ConsultaRepository repository;
    private final PacienteService pacienteService;
    private final ProcedimentoService procedimentoService;

    public ConsultaService() {
        this.repository = new ConsultaRepositoryImpl();
        this.pacienteService = new PacienteService();
        this.procedimentoService = new ProcedimentoService();
    }

    public void salvar(Consulta consulta) {
        validarConsulta(consulta);
        if (consulta.getId() == null || findById(consulta.getId()).isEmpty()) {
            repository.save(consulta);
        } else {
            repository.update(consulta);
        }
    }

    public void excluir(String id) {
        repository.delete(id);
    }

    public Optional<Consulta> findById(String id) {
        return repository.findById(id);
    }

    public List<Consulta> listarTodas() {
        return repository.findAll();
    }

    public List<Consulta> buscarPorPaciente(String pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }

    public List<Consulta> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return repository.findByPeriodo(inicio, fim);
    }

    public double totalFaturadoNoMes() {
        LocalDateTime inicio = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime fim = YearMonth.now().atEndOfMonth().atTime(23, 59, 59);
        return repository.somaValorPorPeriodo(inicio, fim);
    }

    public void registrarConsultaComNovoPaciente(String nomePaciente, String endereco, String email, String celular, 
                                                  LocalDateTime dataHora, Procedimento procedimento, String observacoes) {
        Paciente novo = new Paciente();
        novo.setNome(nomePaciente);
        novo.setEndereco(endereco);
        novo.setEmail(email);
        novo.setCelular(celular);
        pacienteService.salvar(novo);

        Consulta consulta = new Consulta();
        consulta.setPacienteId(novo.getId());
        consulta.setDataHora(dataHora);
        consulta.setNomeProcedimento(procedimento.getNome());
        consulta.setValorProcedimento(procedimento.getValor());
        consulta.setObservacoes(observacoes);
        salvar(consulta);
    }

    public void registrarConsultaComProcedimentoExistente(String pacienteId, LocalDateTime dataHora, String procedimentoId, String observacoes) {
        Procedimento proc = procedimentoService.findById(procedimentoId)
                .orElseThrow(() -> new IllegalArgumentException("Procedimento não encontrado"));
        Consulta consulta = new Consulta();
        consulta.setPacienteId(pacienteId);
        consulta.setDataHora(dataHora);
        consulta.setNomeProcedimento(proc.getNome());
        consulta.setValorProcedimento(proc.getValor());
        consulta.setObservacoes(observacoes);
        salvar(consulta);
    }

    public void registrarConsultaComProcedimentoManual(String pacienteId, LocalDateTime dataHora, String nomeProcedimento, double valor, String observacoes) {
        Consulta consulta = new Consulta();
        consulta.setPacienteId(pacienteId);
        consulta.setDataHora(dataHora);
        consulta.setNomeProcedimento(nomeProcedimento);
        consulta.setValorProcedimento(valor);
        consulta.setObservacoes(observacoes);
        salvar(consulta);
    }

    private void validarConsulta(Consulta consulta) {
        if (consulta.getPacienteId() == null || consulta.getPacienteId().trim().isEmpty()) {
            throw new IllegalArgumentException("Paciente é obrigatório");
        }
        if (consulta.getDataHora() == null) {
            throw new IllegalArgumentException("Data e hora são obrigatórias");
        }
        if (consulta.getNomeProcedimento() == null || consulta.getNomeProcedimento().trim().isEmpty()) {
            throw new IllegalArgumentException("Procedimento é obrigatório");
        }
        if (consulta.getValorProcedimento() <= 0) {
            throw new IllegalArgumentException("Valor do procedimento deve ser positivo");
        }
    }
}