import * as React from "react";
import dayjs, { Dayjs } from "dayjs";
// import Badge from "@mui/material/Badge";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
// import { PickersDay, PickersDayProps } from "@mui/x-date-pickers/PickersDay";
import { DateCalendar } from "@mui/x-date-pickers/DateCalendar";
import { DayCalendarSkeleton } from "@mui/x-date-pickers/DayCalendarSkeleton";

// const data: Record<number, { vegetables: string[] }> = {
//   0: { vegetables: ["peteršilj za koren"] },
//   1: {
//     vegetables: [
//       "gomoljna zelena",
//       "peteršilj za koren",
//       "koleraba",
//       "čebula (seme)",
//       "zgodnje zelje",
//       "cvetača",
//       "ohrovt listnati ohrovt in nadzemna kolerabica",
//       "blitva",
//       "spomladanska solata",
//       "por",
//       "brokoli",
//       "paradižnik",
//       "paprika",
//       "feferon",
//       "čili",
//     ],
//   },
//   2: {
//     vegetables: [
//       "koleraba",
//       "gomoljna zelena",
//       "peteršilj za koren",
//       "zgodnje zelje",
//       "cvetača",
//       "ohrovt listnati ohrovt in nadzemna kolerabica",
//       "blitva",
//       "spomladanska solata",
//       "por",
//       "brokoli",
//       "paradižnik",
//       "paprika",
//       "feferon",
//       "čili",
//     ],
//   },
//   3: {
//     vegetables: [
//       "špinača",
//       "grah",
//       "peteršilj za koren",
//       "zgodnje zelje",
//       "cvetača",
//       "ohrovt listnati ohrovt in nadzemna kolerabica",
//       "blitva",
//       "spomladanska solata",
//       "por",
//       "brokoli",
//       "paradižnik",
//       "paprika",
//       "feferon",
//       "čili",
//     ],
//   },
//   4: {
//     vegetables: [
//       "špinača",
//       "grah",
//       "peteršilj za koren",
//       "cvetača",
//       "ohrovt listnati ohrovt in nadzemna kolerabica",
//       "blitva",
//       "spomladanska solata",
//       "por",
//       "brokoli",
//       "paradižnik",
//       "paprika",
//       "feferon",
//       "čili",
//     ],
//   },
//   5: {
//     vegetables: [
//       "grah",
//       "paradižnik",
//       "paprika",
//       "feferon",
//       "čili",
//       "ohrovt listnati ohrovt in nadzemna kolerabica",
//       "blitva",
//       "spomladanska solata",
//       "por",
//       "brokoli",
//     ],
//   },
//   6: {
//     vegetables: [
//       "paradižnik",
//       "paprika",
//       "feferon",
//       "čili",
//       "ohrovt listnati ohrovt in nadzemna kolerabica",
//       "blitva",
//       "spomladanska solata",
//       "por",
//       "brokoli",
//     ],
//   },
//   7: {
//     vegetables: [
//       "paradižnik",
//       "paprika",
//       "feferon",
//       "čili",
//       "ohrovt listnati ohrovt in nadzemna kolerabica",
//       "blitva",
//       "spomladanska solata",
//       "por",
//       "brokoli",
//     ],
//   },
//   8: {
//     vegetables: [
//       "paradižnik",
//       "paprika",
//       "feferon",
//       "čili",
//       "ohrovt listnati ohrovt in nadzemna kolerabica",
//       "blitva",
//       "spomladanska solata",
//       "por",
//       "brokoli",
//       "zgodnje zelje",
//     ],
//   },
//   9: {
//     vegetables: [
//       "zgodnje zelje",
//       "cvetača",
//       "ohrovt listnati ohrovt in nadzemna kolerabica",
//       "blitva",
//       "spomladanska solata",
//       "por",
//       "brokoli",
//     ],
//   },
//   10: {
//     vegetables: [
//       "zgodnje zelje",
//       "cvetača",
//       "ohrovt listnati ohrovt in nadzemna kolerabica",
//       "blitva",
//       "spomladanska solata",
//       "por",
//     ],
//   },
//   11: {
//     vegetables: [
//       "peteršilj za koren",
//       "zgodnje zelje",
//       "cvetača",
//       "ohrovt listnati ohrovt in nadzemna kolerabica",
//       "blitva",
//     ],
//   },
// };

const initialValue = dayjs(); // današnji datum

function Calender() {
  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <DateCalendar
        defaultValue={initialValue}
        renderLoading={() => <DayCalendarSkeleton />}
      />
    </LocalizationProvider>
  );
}

export default Calender;
