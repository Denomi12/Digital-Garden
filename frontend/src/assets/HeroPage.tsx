import HeaderHero from "./HeaderHero";
import TitleArea from "./TitleArea";
import styles from "./HeroPage.module.css";

function HeroPage() {
    return (
        <div className={styles.heroPageContainer}>
            <HeaderHero/>
            <TitleArea/>
        </div>
    )
}

export default HeroPage;