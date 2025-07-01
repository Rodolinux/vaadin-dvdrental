package com.example.application.services;

import com.example.application.data.entity.Staff;
import com.example.application.repositories.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    @Autowired
    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    /**
     * Obtiene una página de personal, cargando todas las relaciones.
     * @param pageable Objeto Pageable que contiene información de paginación.
     * @return Una página de personal.
     */
    public Page<Staff> list(Pageable pageable) {
        return staffRepository.findAllWithAllRelations(pageable);
    }

    /**
     * Obtiene el número total de personal.
     * @return El número total de personal en la base de datos.
     */
    public long count() {
        return staffRepository.count();
    }

    /**
     * Obtiene un miembro del personal por su ID, cargando todas las relaciones.
     * @param id El ID del miembro del personal.
     * @return Un Optional que contiene el personal si se encuentra, o vacío si no.
     */
    public Optional<Staff> getStaffById(Integer id) {
        return staffRepository.findByIdWithAllRelations(id);
    }

    /**
     * Guarda un nuevo miembro del personal o actualiza uno existente.
     * Si el staffId es nulo, se guarda como un nuevo miembro.
     * Si el staffId existe, se actualiza el miembro existente.
     * La columna 'last_update' se establece automáticamente.
     * @param staff El miembro del personal a guardar/actualizar.
     * @return El miembro del personal guardado/actualizado.
     */
    public Staff saveStaff(Staff staff) {
        if (staff.getLastUpdate() == null) {
            staff.setLastUpdate(LocalDateTime.now());
        } else {
            staff.setLastUpdate(LocalDateTime.now());
        }
        return staffRepository.save(staff);
    }

    /**
     * Elimina un miembro del personal por su ID.
     * @param id El ID del miembro del personal a eliminar.
     */
    public void deleteStaff(Integer id) {
        staffRepository.deleteById(id);
    }

    /**
     * Busca personal por un fragmento de nombre (sin distinción entre mayúsculas y minúsculas), con paginación.
     * @param firstName El fragmento de nombre a buscar.
     * @param pageable Objeto Pageable para la paginación.
     * @return Una página de personal que coincide.
     */
    public Page<Staff> findStaffByFirstName(String firstName, Pageable pageable) {
        return staffRepository.findByFirstNameContainingIgnoreCase(firstName, pageable);
    }

    /**
     * Busca personal por un fragmento de apellido (sin distinción entre mayúsculas y minúsculas), con paginación.
     * @param lastName El fragmento de apellido a buscar.
     * @param pageable Objeto Pageable para la paginación.
     * @return Una página de personal que coincide.
     */
    public Page<Staff> findStaffByLastName(String lastName, Pageable pageable) {
        return staffRepository.findByLastNameContainingIgnoreCase(lastName, pageable);
    }
}