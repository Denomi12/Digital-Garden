import { useState, useEffect } from "react";
import styles from "../../stylesheets/Forum.module.css";
import Question from "./Question";
import { useNavigate } from "react-router-dom";

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
};

function Forum() {
  const [questions, setQuestions] = useState<QuestionType[]>([]);
  const navigate = useNavigate();

  useEffect(function () {
    const getPhotos = async function () {
      const res = await fetch("http://localhost:3001/question");
      const data = await res.json();
      setQuestions(data);
    };
    getPhotos();
  }, []);

  function addQuestion() {
    navigate("/addQuestion");
  }

  return (
    <>
      <button className={styles.addQuestionBtn} onClick={addQuestion}>
        Ask Question
      </button>
      <div className={styles.forumWrapper}>
        {questions.map((question) => (
          <Question key={question._id} question={question} />
        ))}
      </div>
    </>
  );
}

export default Forum;
