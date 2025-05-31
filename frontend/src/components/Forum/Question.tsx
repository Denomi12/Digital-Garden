import { QuestionType } from "./Forum";
import { useState } from "react";
import styles from "../../stylesheets/Question.module.css";
import LikeDislike from "./LikeDislike";
// import { NavLink } from "react-router-dom";

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

  return (
    <div className={styles.container}>
      <div className={styles.leftSide}>
        <LikeDislike id={question._id} />
      </div>
      <div
        className={styles.rightSide}
        onClick={() => setExpanded(!expanded)}
        style={{ cursor: "pointer" }}
      >
        {expanded && (
          <div className={styles.expandedTitle}>
            <div className={styles.title}>{question.title}</div>
            <div>img</div>
          </div>
        )}
        {!expanded && <div className={styles.title}>{question.title}</div>}
        <div className={styles.summary}>{question.question}</div>

        <hr className={styles.line}></hr>
        {!expanded && (
          <div className={styles.footer}>
            <div className={styles.footerLeft}>
              <div>img</div>
              <div className={styles.owner}>
                Posted By <strong>{question.owner.username}</strong>
              </div>
              <div>{timeAgo(question.createdAt)}</div>
            </div>

            <div className={styles.comments}>
              <div>LOGO</div>
              <div>{question.comments.length}+</div>
            </div>
          </div>
        )}

        {expanded && (
          <div className={styles.commentsSection}>
            {question.comments.length === 0 && <div>No comments yet.</div>}
            {question.comments.map((comment) => (
              <div key={comment._id} className={styles.comment}>
                <p>{comment.body}</p>
                <small>By {comment.owner.username}</small>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default Question;
