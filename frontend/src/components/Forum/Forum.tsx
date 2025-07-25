import { useState, useEffect } from "react";
import styles from "../../stylesheets/Forum.module.css";
import Question from "./Question";
import { useNavigate } from "react-router-dom";
import Chat from "../Chat/Chat.tsx";

export type CommentType = {
  _id: string;
  body: string;
  owner: UserSummary;
  createdAt: Date;
};

type UserSummary = {
  _id: string;
  username: string;
};

export type QuestionType = {
  _id: string;
  title: string;
  question: string;
  summary: string;
  likes: number;
  owner: UserSummary;
  likedBy: UserSummary[];
  dislikedBy: UserSummary[];
  createdAt: Date;
  comments: CommentType[];
  botGenerated: any;
};

function Forum() {
  const [questions, setQuestions] = useState<QuestionType[]>([]);
  const navigate = useNavigate();

  const getQuestions = async function () {
    const res = await fetch(`${import.meta.env.VITE_API_BACKEND_URL}/question`);
    const data = await res.json();
    setQuestions(data);
  };

  useEffect(function () {
    getQuestions();
  }, []);

  function addQuestion() {
    navigate("/addQuestion");
  }

  return (
    <div className={styles.forum}>
      <div className={styles.leftSide}>
        <div className={styles.forumWrapper}>
          {questions.map((question) => (
            <Question key={question._id} question={question} />
          ))}
        </div>
      </div>
      <div className={styles.rightSide}>
        <button className={styles.addQuestionBtn} onClick={addQuestion}>
          + Ask Question
        </button>
        <div className={styles.chat}>
          <Chat refreshForum={getQuestions} />
        </div>
      </div>
    </div>
  );
}

export default Forum;
