import { useState, useEffect } from "react";
import styles from "../../stylesheets/Forum.module.css";
import Question from "./Question";
import { useNavigate } from "react-router-dom";

type UserSummary = {
  _id: string;
};

type QuestionType = {
  _id: string;
  title: string;
  question: string;
  likes: number;
  owner: UserSummary;
  likedBy: UserSummary[];
  dislikedBy: UserSummary[];
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
      <button onClick={addQuestion}>Add Question</button>
      <div className={styles.container}>
        <div className={styles.questionGrid}>
          {questions.map((question) => (
            <div key={question._id} className={styles.questionItem}>
              <Question />
            </div>
          ))}
        </div>
      </div>
    </>
  );
}

export default Forum;
