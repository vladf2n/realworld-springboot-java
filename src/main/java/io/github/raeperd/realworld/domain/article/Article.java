package io.github.raeperd.realworld.domain.article;

import io.github.raeperd.realworld.domain.article.comment.Comment;
import io.github.raeperd.realworld.domain.article.import_article.Import;
import io.github.raeperd.realworld.domain.user.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Table(name = "articles")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Article {

    @GeneratedValue(strategy = IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = EAGER)
    private User author;

    @Column(name = "author_name")
    private String authorName;

    @JoinColumn(name = "import_id", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = EAGER)
    private Import importArticle;

    @Embedded
    private ArticleContents contents;

    @Column(name = "created_at")
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;

    @JoinTable(name = "article_favorites",
            joinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false))
    @ManyToMany(fetch = EAGER, cascade = PERSIST)
    private Set<User> userFavorited = new HashSet<>();

    @OneToMany(mappedBy = "article", cascade = {PERSIST, REMOVE})
    private Set<Comment> comments = new HashSet<>();

    @Transient
    private boolean favorited = false;

    public Article(User author, ArticleContents contents) {
        this.author = author;
        this.contents = contents;
    }

    protected Article() {
    }

    public Article afterUserFavoritesArticle(User user) {
        userFavorited.add(user);
        return updateFavoriteByUser(user);
    }

    public Article afterUserUnFavoritesArticle(User user) {
        userFavorited.remove(user);
        return updateFavoriteByUser(user);
    }

    public Comment addComment(User author, String body) {
        final var commentToAdd = new Comment(this, author, body);
        comments.add(commentToAdd);
        return commentToAdd;
    }

    public void removeCommentByUser(User user, long commentId) {
        final var commentsToDelete = comments.stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        if (!user.equals(author) || !user.equals(commentsToDelete.getAuthor())) {
            throw new IllegalAccessError("Not authorized to delete comment");
        }
        comments.remove(commentsToDelete);
    }

    public void updateArticle(ArticleUpdateRequest updateRequest) {
        contents.updateArticleContentsIfPresent(updateRequest);
    }

    public Article updateFavoriteByUser(User user) {
        favorited = userFavorited.contains(user);
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public ArticleContents getContents() {
        return contents;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public int getFavoritedCount() {
        return userFavorited.size();
    }

    public boolean isFavorited() {
        return favorited;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Import getImportArticle() {
        return importArticle;
    }

    public void setImportArticle(Import importArticle) {
        this.importArticle = importArticle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var article = (Article) o;
        return author.equals(article.author) && contents.getTitle().equals(article.contents.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, contents.getTitle());
    }
}
