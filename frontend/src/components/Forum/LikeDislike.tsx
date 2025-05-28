import { useEffect, useState } from "react";
import { FaArrowUp, FaArrowDown } from "react-icons/fa";
import styles from "../../stylesheets/LikeDislike.module.css";

type LikeDislikeProps = {
  id: string;
};

function LikeDislike({ id }: LikeDislikeProps) {
  const [likes, setLikes] = useState(0);
  const [liked, setLiked] = useState(false);
  const [disliked, setDisliked] = useState(false);
  const [userId, setUserId] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      const [questionRes, meRes] = await Promise.all([
        fetch(`${import.meta.env.VITE_API_BACKEND_URL}/question/${id}`),
        fetch(`${import.meta.env.VITE_API_BACKEND_URL}/user/me`, {
          credentials: "include",
        }),
      ]);

      const questionData = await questionRes.json();
      const meData = await meRes.json();

      setLikes(questionData.likes);
      setUserId(meData.user?.id || "");

      if (meData.user) {
        const uid = meData.user.id;
        setLiked(questionData.likedBy.includes(uid));
        setDisliked(questionData.dislikedBy.includes(uid));
      }
    };

    fetchData();
  }, []);

  async function handleLike() {
    const res = await fetch(`http://localhost:3001/question/like/${id}`, {
      method: "POST",
      credentials: "include",
    });

    if (res.ok) {
      const data = await res.json();
      setLikes(data.likes);
      setLiked(data.likedBy.includes(userId));
      setDisliked(data.dislikedBy.includes(userId));
    }
  }

  async function handleDislike() {
    const res = await fetch(`http://localhost:3001/question/dislike/${id}`, {
      method: "POST",
      credentials: "include",
    });

    if (res.ok) {
      const data = await res.json();
      setLikes(data.likes);
      setLiked(data.likedBy.includes(userId));
      setDisliked(data.dislikedBy.includes(userId));
    }
  }

  return (
    <div className={styles.voteBox}>
      <button
        onClick={handleLike}
        className={`${styles.arrowButton} ${liked ? styles.active : ""}`}
      >
        <FaArrowUp />
      </button>

      <div className={styles.likeCount}>{likes}</div>

      <button
        onClick={handleDislike}
        className={`${styles.arrowButton} ${disliked ? styles.active : ""}`}
      >
        <FaArrowDown />
      </button>
    </div>
  );
}

export default LikeDislike;
