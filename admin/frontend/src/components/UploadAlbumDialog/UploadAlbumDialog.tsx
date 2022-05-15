import React, { useState } from "react";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";

interface Props {
  isOpen: boolean;
  onCancel: () => void;
  onUpload: (artist: string, album: string, albumZipFile: File) => void;
}

const UploadAlbumDialog: React.FC<Props> = ({ isOpen, onCancel, onUpload }) => {
  const [artist, setArtist] = useState<string>("");
  const [album, setAlbum] = useState<string>("");
  const [albumZipFile, setAlbumZipFile] = useState<File>();

  const onConfirm = () => {
    if (!!artist && !!album && !!albumZipFile) {
      onUpload(artist, album, albumZipFile);
    }
  };

  return (
    <Dialog open={isOpen} onClose={() => onCancel()}>
      <DialogTitle>Upload New Album</DialogTitle>
      <DialogContent>
        <Box sx={{ display: "flex", flexDirection: "column" }}>
          <TextField
            margin="dense"
            label="Artist"
            type="text"
            variant="standard"
            onChange={(e) => setArtist(e.target.value)}
            error={artist === ""}
          />
          <TextField
            margin="dense"
            label="Album"
            type="text"
            variant="standard"
            onChange={(e) => setAlbum(e.target.value)}
            error={album === ""}
          />
        </Box>
        <Button component="label">
          Select album zip file
          <input
            type="file"
            hidden
            onChange={(e) => setAlbumZipFile(e.target.files![0])}
          />
        </Button>
      </DialogContent>
      <DialogActions>
        <Button onClick={() => onCancel()}>Cancel</Button>
        <Button onClick={() => onConfirm()} autoFocus>
          Upload
        </Button>
      </DialogActions>
    </Dialog>
  );
};
export default UploadAlbumDialog;
