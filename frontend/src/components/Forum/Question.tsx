import { QuestionType } from "./Forum";
import styles from "../../stylesheets/Question.module.css";

type QuestionProps = {
  question: QuestionType;
};

function Question({ question }: QuestionProps) {
  return (
    <div className={styles.container}>
      <h3 className={styles.title}>{question.title}</h3>
      <p className={styles.summary}>{question.summary}</p>
      <div className={styles.footer}>
        <div className={styles.likes}>{question.likes} likes</div>
        <div className={styles.owner}>
          Posted By <strong>{question.owner.username}</strong>,{" "}
          {new Date(question.createdAt).toLocaleDateString("sl-SI")}
        </div>
      </div>
    </div>
  );
}

export default Question;
