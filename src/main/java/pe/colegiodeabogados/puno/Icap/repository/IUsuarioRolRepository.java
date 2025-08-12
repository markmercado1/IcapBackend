package pe.colegiodeabogados.puno.Icap.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.colegiodeabogados.puno.Icap.model.UsuarioRol;
import pe.colegiodeabogados.puno.Icap.model.UsuarioRolPK;


import java.util.List;

public interface IUsuarioRolRepository extends ICrudGenericRepository<UsuarioRol, UsuarioRolPK>{
    @Query("SELECT ur FROM UsuarioRol ur WHERE ur.usuario.users = :users")//Consulta JPQL
    List<UsuarioRol> findOneByUsuarioUsers(@Param("users") String users);


}