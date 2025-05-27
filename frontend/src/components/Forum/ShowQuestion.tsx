import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
// import LikeDislikeButton from "./LikeButton.js";
// import Comments from "./Comments.js";
import styles from "../../stylesheets/ShowQuestion.module.css";

type UserSummary = {
  _id: string;
  username: string;
};

type QuestionType = {
  _id: string;
  title: string;
  question: string;
  summary: string;
  likes: number;
  owner: UserSummary;
  likedBy: UserSummary[];
  dislikedBy: UserSummary[];
  createdAt: Date;
};

function ShowQuestion() {
  const { id } = useParams();
  const [question, setQuestion] = useState<QuestionType | null>(null);

  useEffect(function () {
    const getQuestion = async function () {
      const res = await fetch(
        `${import.meta.env.VITE_API_BACKEND_URL}/question/${id}`
      );
      const data = await res.json();
      setQuestion(data);
    };

    getQuestion();
  }, []);

  return (
    <div className={styles.photoContainer}>
      {question && (
        <div className={styles.photoContent}>
          <div className={styles.photoAuthor}>{question.title}</div>
          <div className={styles.photoText}>{question.question}</div>
          <div className={styles.photoOwner}>
            Posted by <strong>{question.owner.username}</strong>
          </div>
          <div className={styles.photoDate}>
            {new Date(question.createdAt).toLocaleDateString("sl-SI")}
          </div>
        </div>
      )}
    </div>
  );
}

export default ShowQuestion;
