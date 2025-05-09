import styles from "./TitleArea.module.css"
import { useNavigate } from "react-router-dom";

const TitleArea = () => {
    const navigate = useNavigate()

    const handleClick = () => {
        navigate("/garden");
    }

    return (
        <div className={styles.titleAreaContainer}>
            <div className={styles.titleHero}>
                <h1>
                    TVOJ DIGITALNI VRT
                </h1>
            </div>
            <div className={styles.descriptionHero}>
                <h2>
                    Spremljaj rastline, vreme in razvoj svojega vrta v realnem času
                </h2>
            </div>
            <button onClick={handleClick} className={styles.buttonHero}>
                Razišči zdaj
            </button>
        </div>
    );
};

export default TitleArea;