import { useEffect, useState } from "react";
import { CommentType } from "./Forum";
import styles from "../../stylesheets/Comment.module.css";
import LikeDislikeComment from "./LikeDislikeComment";

type CommentProps = {
  commentId: string;
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

function Comment({ commentId }: CommentProps) {
  const [comment, setComment] = useState<CommentType | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchComment() {
      try {
        const res = await fetch(
          `${import.meta.env.VITE_API_BACKEND_URL}/comment/${commentId}`,
          { credentials: "include" }
        );
        const data = await res.json();
        if (data._id) {
          setComment(data);
        } else {
          setError("Comment not found");
        }
      } catch (err) {
        setError("Error fetching comment");
      }
    }

    fetchComment();
  }, []);

  if (error) return <div className={styles.error}>{error}</div>;
  if (!comment) return;

  return (
    <div className={styles.container}>
      <div className={styles.leftSide}>
        <LikeDislikeComment id={comment._id} />
      </div>
      <div className={styles.rightSide}>
        <div className={styles.body}>{comment.body}</div>
        <hr className={styles.line} />
        <div className={styles.footer}>
          <div className={styles.footerLeft}>
            <div>img</div>
            <div className={styles.owner}>
              Posted By <strong>{comment.owner.username}</strong>
            </div>
            <div>{timeAgo(comment.createdAt)}</div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Comment;
