import React from "react";
import { Box, CircularProgress } from "@mui/material";

interface Props {}

const LoadingScreen: React.FC<Props> = () => {
  return (
    <Box
      sx={{
        position: "absolute",
        top: 0,
        left: 0,
        width: "100vw",
        height: "100vh",
        backgroundColor: "black",
        opacity: "0.7",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      <CircularProgress />
    </Box>
  );
};
export default LoadingScreen;
