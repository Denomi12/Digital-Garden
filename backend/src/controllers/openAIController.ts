import axios from "axios";
import { Request, Response, NextFunction } from "express";
import cropController from "./cropController";
import CropModel, { Crop } from "../models/CropModel";
import mongoose, { Types } from "mongoose";
import { CropAI, CropData } from "../types/gardenData";

const generateCrop = async (req: Request, res: Response): Promise<void> => {
  try {
    const OPENAI_API_URL = process.env.OPENAI_API_URL;
    const OPENAI_API_KEY = process.env.OPENAI_API_KEY;

    if (!OPENAI_API_URL || !OPENAI_API_KEY) {
      res.status(500).json({ error: "OpenAI API configuration is missing." });
      return;
    }

    const numberOfCrops = req.body.amount;
    if (
      !numberOfCrops ||
      typeof numberOfCrops !== "number" ||
      numberOfCrops <= 0
    ) {
      res.status(400).json({ error: "Invalid 'number' in request body." });
      return;
    }

    const cropNames = (await CropModel.find().select("name")).map(
      (crop) => crop.name
    );
    const prompt = `Ustvari podatke za ${numberOfCrops} rastlin v JSON formatu s sledečimi polji:
[
  {
    "name": "ime rastline v slovenščini",
    "latinName": "latinsko ime",
    "watering": {
      "frequency": "besedni opis pogostosti zalivanja (npr. '1-krat na teden', 'Vsak dan', 'redko')",
      "amount": število litrov vode na teden (decimalka)
    }
  }
]
Naj se imena ne ponavljajo, generiraj nove rastline, ne uporabljaj rastlin s tega seznama: ${cropNames}
Vrni samo JSON. Brez razlage ali dodatnega teksta. 
`;
    const response = await axios.post(
      OPENAI_API_URL!!,
      {
        model: "gpt-3.5-turbo",
        messages: [
          {
            role: "system",
            content:
              "Generiraš samo ime, latinsko ime in podatke o zalivanju za rastline na vrtu.",
          },
          { role: "user", content: prompt },
        ],
        temperature: 0.7,
      },
      {
        headers: {
          Authorization: `Bearer ${OPENAI_API_KEY}`,
          "Content-Type": "application/json",
        },
      }
    );

    const apiResponse = response.data?.choices?.[0]?.message?.content;
    if (!apiResponse) {
      res.status(502).json({ error: "Invalid response from OpenAI API." });
      return;
    }

    let cropsArray: CropAI[];
    try {
      cropsArray = JSON.parse(apiResponse);
      if (!Array.isArray(cropsArray)) {
        throw new Error("Expected JSON array");
      }
    } catch (parseError) {
      res
        .status(500)
        .json({ error: "Failed to parse OpenAI response JSON as array." });
      return;
    }

    // Wrap array into expected CropData shape
    const cropsData: CropData = { crops: cropsArray };

    const parsedCrops = await parseCrops(cropsData);

    res.status(200).json({ success: true, crops: parsedCrops });
  } catch (error) {
    console.error("generateCrop error:", error);
    res.status(500).json({ error: "Internal server error." });
  }
};

function splitRandomIds(ids: Types.ObjectId[]): {
  randomBad: Types.ObjectId[];
  randomGood: Types.ObjectId[];
} {
  const shuffled = [...ids].sort(() => Math.random() - 0.5);

  const maxCount = Math.min(3, shuffled.length);

  const badCount = Math.floor(Math.random() * (maxCount + 1)); // 0 to 3
  const goodCount = Math.floor(Math.random() * (maxCount + 1)); // 0 to 3

  const totalToTake = badCount + goodCount;
  const selected = shuffled.slice(0, totalToTake);

  const randomBad = selected.slice(0, badCount);
  const randomGood = selected.slice(badCount, badCount + goodCount);

  return { randomBad, randomGood };
}

const formatCrop = (shuffledIds: Types.ObjectId[], aiCrop: CropAI) => {
  const months = [
    "januar",
    "februar",
    "marec",
    "april",
    "maj",
    "junij",
    "julij",
    "avgust",
    "september",
    "oktober",
    "november",
    "december",
  ];

  const month = months[Math.floor(Math.random() * months.length)];
  const { randomBad, randomGood } = splitRandomIds(shuffledIds);

  const generatedCrop = {
    name: aiCrop.name,
    latinName: aiCrop.latinName,
    goodCompanions: randomGood,
    badCompanions: randomBad,
    plantingMonth: month,
    watering: aiCrop.watering,
  };

  return generatedCrop;
};

// function takes in OpenAI response and generates an array of crops
const parseCrops = async (cropsData: CropData) => {
  const shuffledIds: Types.ObjectId[] = (await CropModel.find().select("_id"))
    .map((crop: Crop) => crop._id as Types.ObjectId)
    .sort(() => Math.random() - 0.5);

  const parsedData = cropsData.crops.map((element: CropAI) =>
    formatCrop(shuffledIds, element)
  );

  console.log(parsedData);

  return parsedData;
};

const generateChat = async (req: Request, res: Response): Promise<void> => {
  try {
    const OPENAI_API_URL = process.env.OPENAI_API_URL;
    const OPENAI_API_KEY = process.env.OPENAI_API_KEY;

    if (!OPENAI_API_URL || !OPENAI_API_KEY) {
      res.status(500).json({ error: "OpenAI API configuration is missing." });
      return;
    }
    const question = req.body.question;
    const prompt = `Odgovori prosim na naslednje vprašanje: ${question}. 
    `;

    const response = await axios.post(
      OPENAI_API_URL!!,
      {
        model: "gpt-3.5-turbo",
        messages: [
          {
            role: "system",
            content:
              "Odgovarjaj samo na vprašanja, ki se nanašajo na vrtnarjenje. Če vprašanje nima nobene povezave z rastlinami, vrtninami, pridelavo ali nego vrta, vljudno povej, da ni relevantno. Odgovori naj bodo kratki, jasni in jedrnati – brez dolgih razlag.",
          },
          { role: "user", content: prompt },
        ],
        temperature: 0.7,
      },
      {
        headers: {
          Authorization: `Bearer ${OPENAI_API_KEY}`,
          "Content-Type": "application/json",
        },
      }
    );

    const apiResponse = response.data?.choices?.[0]?.message?.content;
    if (!apiResponse) {
      res.status(502).json({ error: "Invalid response from OpenAI API." });
      return;
    }
    res.status(200).json({ success: true, response: apiResponse });
  } catch (error) {
    res.status(500).json({ error: "Internal server error." });
  }
};

export default {
  generateCrop,
  generateChat,
};
