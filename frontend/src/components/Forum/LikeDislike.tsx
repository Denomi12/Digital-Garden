import { useEffect, useState } from "react";
import { FaArrowUp, FaArrowDown } from "react-icons/fa";
import styles from "../../stylesheets/LikeDislike.module.css";

type LikeDislikeProps = {
  id: string;
};

function LikeDislike({ id }: LikeDislikeProps) {
  const [likes, setLikes] = useState(0);

  useEffect(function () {
    const getLikes = async function () {
      const res = await fetch(
        `${import.meta.env.VITE_API_BACKEND_URL}/question/${id}`
      );
      const data = await res.json();
      console.log(data);
      setLikes(data.likes);
    };

    getLikes();
  }, []);

  async function handleLike() {
    const res = await fetch(`http://localhost:3001/question/like/${id}`, {
      method: "POST",
      credentials: "include",
    });

    if (res.ok) {
      const data = await res.json();
      setLikes(data.likes);
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
    }
  }

  return (
    <div className={styles.voteBox}>
      <button onClick={handleLike} className={styles.arrowButton}>
        <FaArrowUp />
      </button>
      <div className={styles.likeCount}>{likes}</div>
      <button onClick={handleDislike} className={styles.arrowButton}>
        <FaArrowDown />
      </button>
    </div>
  );
}

export default LikeDislike;
