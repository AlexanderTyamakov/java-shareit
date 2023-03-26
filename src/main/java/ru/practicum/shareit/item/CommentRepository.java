package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemIdIsOrderByCreatedDesc(long id);

    List<Comment> findAllByItemIdIn(List<Long> id);

    Comment findFirstByOrderByIdDesc();


}
