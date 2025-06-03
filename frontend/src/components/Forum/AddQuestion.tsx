import { useState } from "react";
import { Navigate } from "react-router";
import styles from "../../stylesheets/AddQuestion.module.css";

function AddQuestion() {
  const [title, setTitle] = useState("");
  const [questionMessage, setQuestionMessage] = useState("");
  const [uploaded, setUploaded] = useState(false);

  async function onSubmit(e: any) {
    e.preventDefault();
    if (!title) {
      alert("Vnesite naslov!");
      return;
    }

    if (!questionMessage) {
      alert("Vnesite sporocilo!");
      return;
    }

    const res = await fetch(
      `${import.meta.env.VITE_API_BACKEND_URL}/question`,
      {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          title,
          questionMessage,
        }),
      }
    );

    const data = await res.json();
    setUploaded(true);
  }

  return (
    <div className={styles.pageWrapper}>
      {uploaded && <Navigate replace to="/forum" />}
      <div className={styles.formHeader}>Add new questions</div>
      <form className={styles.formGroup} onSubmit={onSubmit}>
        <input
          type="text"
          className={styles.formControl}
          name="title"
          placeholder="Question Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <textarea
          className={styles.formControl}
          name="question"
          placeholder="Your Question"
          rows={6}
          value={questionMessage}
          onChange={(e) => setQuestionMessage(e.target.value)}
        />
        <input
          className={styles.submitButton}
          type="submit"
          value="Add Question"
        />
      </form>
    </div>
  );
}

export default AddQuestion;
