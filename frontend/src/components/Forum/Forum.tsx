import { useState, useEffect } from "react";

function Forum() {
  const [questions, setQuestions] = useState([]);

  useEffect(function () {
    const getPhotos = async function () {
      const res = await fetch("http://localhost:3001/questions");
      const data = await res.json();
      setPhotos(data);
    };
    getPhotos();
  }, []);

  return (
    <div className={styles.container}>
      <div className={styles.photoGrid}>
        {photos.map((photo) => (
          <div key={photo._id} className={styles.photoItem}>
            <Photo photo={photo} />
          </div>
        ))}
      </div>
    </div>
  );
}

export default Forum;
