package vn.codegym.BE_BookOnline.repository;

import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByNameRole(String customer);
}
