package by.ycovich.repository;

import by.ycovich.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query("""
select t from Token t inner join UserEntity u on t.user.id = u.id
where u.id = :user_id and (t.expired = false or t.revoked = false)
""")
    List<Token> findAllValidTokensByUserId(Integer user_id);

    Optional<Token> findByToken(String token);
}
