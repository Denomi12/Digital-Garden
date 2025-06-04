import { QuestionType } from "./Forum";
import { useState } from "react";
import styles from "../../stylesheets/Question.module.css";
import LikeDislike from "./LikeDislike";
import Comment from "./Comment";

type QuestionProps = {
  question: QuestionType;
};

function timeAgo(date: Date | string): string {
  const now = new Date();
  const posted = new Date(date);
  const diffMs = now.getTime() - posted.getTime();
  const diffMins = Math.floor(diffMs / (1000 * 60));
  const diffHours = Math.floor(diffMins / 60);
  const diffDays = Math.floor(diffHours / 24);

  if (diffDays >= 1) return `${diffDays} day${diffDays > 1 ? "s" : ""} ago`;
  if (diffHours >= 1) return `${diffHours} hour${diffHours > 1 ? "s" : ""} ago`;
  if (diffMins >= 1) return `${diffMins} minute${diffMins > 1 ? "s" : ""} ago`;
  return "Just now";
}

function Question({ question }: QuestionProps) {
  const [expanded, setExpanded] = useState(false);
  const [newComment, setNewComment] = useState<string>("");
  const [comments, setComments] = useState(question.comments);
  const [error, setError] = useState<string | null>(null);

  async function addComment() {
    try {
      const res = await fetch(
        `${import.meta.env.VITE_API_BACKEND_URL}/comment/${question._id}`,
        {
          method: "POST",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            body: newComment,
          }),
        }
      );

      const data = await res.json();

      if (data._id) {
        setComments((prev) => [...prev, data]);
        setNewComment("");
        setError(null);
      } else {
        setError("Adding a comment failed.");
      }
    } catch (err) {
      setError("Server error: Could not add comment.");
    }
  }

  return (
    <div className={styles.container}>
      <div className={styles.leftSide}>
        <LikeDislike id={question._id} />
      </div>
      <div className={styles.rightSide}>
        {expanded && (
          <div
            className={styles.expandedTitle}
            onClick={() => setExpanded(!expanded)}
          >
            <div className={styles.title}>{question.title}</div>
            Posted By {question.owner.username}
          </div>
        )}
        {!expanded && (
          <div className={styles.title} onClick={() => setExpanded(!expanded)}>
            {question.title}
          </div>
        )}
        <div className={styles.summary} onClick={() => setExpanded(!expanded)}>
          {question.question}
        </div>

        <hr className={styles.line}></hr>
        {!expanded && (
          <div className={styles.footer}>
            <div className={styles.footerLeft}>
              <div className={styles.owner}>
                Posted By <strong>{question.owner.username}</strong>
              </div>
              <div>{timeAgo(question.createdAt)}</div>
            </div>

            <div className={styles.comments}>
              <img className={styles.chatImage} src="/assets/chat.png" />
              <div>{question.comments.length}</div>
            </div>
          </div>
        )}

        {expanded && (
          <>
            <div className={styles.commentsSection}>
              {comments.length === 0 && <div>No comments yet.</div>}
              {comments.map((comment, index) => (
                <Comment key={comment._id || index} commentId={comment._id} />
              ))}
            </div>

            <form
              className={styles.commentForm}
              onSubmit={(e) => {
                e.preventDefault();
                addComment();
              }}
            >
              <input
                type="text"
                className={styles.commentInput}
                placeholder="Add a comment..."
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                required
              />
              <button type="submit" className={styles.commentButton}>
                Add Comment
              </button>
            </form>

            {error && <div className={styles.error}>{error}</div>}
          </>
        )}
      </div>
    </div>
  );
}

export default Question;
