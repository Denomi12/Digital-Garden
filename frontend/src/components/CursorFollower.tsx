import { useEffect, useState } from "react";

type Position = {
  x: number;
  y: number;
};

type CursorFollowerProps = {
  cropImage?: string | null;
  elementImage?: string | null;
};

function CursorFollower({ cropImage, elementImage }: CursorFollowerProps) {
  const [position, setPosition] = useState<Position>({ x: 0, y: 0 });
  const [isCropValid, setIsCropValid] = useState(true);
  const [isElementValid, setIsElementValid] = useState(true);

  useEffect(() => {
    const moveHandler = (e: MouseEvent) => {
      setPosition({ x: e.clientX, y: e.clientY });
    };
    window.addEventListener("mousemove", moveHandler);
    return () => window.removeEventListener("mousemove", moveHandler);
  }, []);

  useEffect(() => {
    setIsCropValid(true);
  }, [cropImage]);

  useEffect(() => {
    setIsElementValid(true);
  }, [elementImage]);

  if ((!cropImage || !isCropValid) && (!elementImage || !isElementValid)) {
    return null;
  }

  return (
    <>
      {elementImage && isElementValid && (
        <img
          src={elementImage}
          alt="element"
          onError={() => setIsElementValid(false)}
          style={{
            position: "fixed",
            top: position.y,
            left: position.x,
            width: "40px",
            height: "40px",
            pointerEvents: "none",
            transform: "translate(-50%, -50%)",
            zIndex: 9998, // background
          }}
        />
      )}
      {cropImage && isCropValid && (
        <img
          src={cropImage}
          alt="crop"
          onError={() => setIsCropValid(false)}
          style={{
            position: "fixed",
            top: position.y,
            left: position.x,
            width: "32px",
            height: "32px",
            pointerEvents: "none",
            transform: "translate(-50%, -50%)",
            zIndex: 9999, // foreground
          }}
        />
      )}
    </>
  );
}

export default CursorFollower;
