import { useState } from "react";
import { Navigate } from "react-router";
import styles from "../../stylesheets/AddQuestion.module.css";

function AddQuestion() {
  const [title, setTitle] = useState("");
  const [questionMessage, setQuestionMessage] = useState("");
  const [summary, setSummary] = useState("");
  const [uploaded, setUploaded] = useState(false);

  //
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

    const res = await fetch("http://localhost:3001/question", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        title,
        questionMessage,
        summary,
      }),
    });

    const data = await res.json();
    console.log(data);
    setUploaded(true);
  }

  return (
    <form className={styles.formGroup} onSubmit={onSubmit}>
      {uploaded ? <Navigate replace to="/forum" /> : ""}
      <input
        type="text"
        className={styles.formControl}
        name="title"
        placeholder="Question Title"
        value={title}
        onChange={(e) => {
          setTitle(e.target.value);
        }}
      />
      <input
        type="text"
        className={styles.formControl}
        name="summary"
        placeholder="Question Summary"
        value={summary}
        onChange={(e) => {
          setSummary(e.target.value);
        }}
      />
      <input
        type="text"
        className={styles.formControl}
        name="question"
        placeholder="Your Question"
        value={questionMessage}
        onChange={(e) => {
          setQuestionMessage(e.target.value);
        }}
      />
      <input
        className={styles.submitButton}
        type="submit"
        name="submit"
        value="Submit"
      />
    </form>
  );
}

export default AddQuestion;
