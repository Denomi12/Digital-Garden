import { useState, useEffect } from "react";
import LikeDislike from "./Forum/LikeDislike";
import styles from "../stylesheets/HotQuestions.module.css";

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

function HotQuestion() {
  const [questions, setQuestions] = useState<QuestionType[]>([]);

  useEffect(function () {
    const getQuestions = async function () {
      const res = await fetch(
        `${import.meta.env.VITE_API_BACKEND_URL}/question/hotQuestion`
      );
      const data = await res.json();
      setQuestions(data);
    };
    getQuestions();
  }, []);

  return (
    <div>
      <div>
        <div className={styles.questionList}>
          {questions.map((question) => (
            <div className={styles.container}>
              <div className={styles.leftSide}>
                <LikeDislike id={question._id} />
              </div>
              <div className={styles.rightSide}>
                <div className={styles.title}>{question.title}</div>
                <div className={styles.summary}>{question.question}</div>

                <hr className={styles.line}></hr>
                <div className={styles.footer}>
                  <div className={styles.footerLeft}>
                    <div className={styles.owner}>
                      Posted By <strong>{question.owner.username}</strong>
                    </div>
                    <div>{timeAgo(question.createdAt)}</div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default HotQuestion;
