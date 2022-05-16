import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
} from "@mui/material";
import React, { useState } from "react";
import { Album } from "../../model/Album";

interface Props {
  isOpen: boolean;
  albums: Album[];
  onCancel: () => void;
  onGenerate: (selectedAlbumId: string, numberOfDownloadCodes: number) => void;
}

const GenerateDownloadCodesDialog: React.FC<Props> = ({
  isOpen,
  albums,
  onCancel,
  onGenerate,
}) => {
  const [selectedAlbum, setSelectedAlbum] = useState<string>("");
  const [selectedDownloadCodes, setSelectedDownloadCodes] = useState<number>();

  const onConfirm = () => {
    if (!!selectedAlbum && !!selectedDownloadCodes) {
      onGenerate(selectedAlbum, selectedDownloadCodes);
    }
  };

  return (
    <Dialog open={isOpen} onClose={() => onCancel()}>
      <DialogTitle>Generate Download Codes</DialogTitle>
      <DialogContent>
        <br />
        <FormControl fullWidth>
          <InputLabel>Album</InputLabel>
          <Select
            label="Album"
            value={selectedAlbum}
            onChange={(e) => setSelectedAlbum(e.target.value)}
          >
            {albums.map((album, idx) => (
              <MenuItem
                key={`album-option-${idx}`}
                value={album.id}
              >{`${album.artist} - ${album.albumName}`}</MenuItem>
            ))}
          </Select>
        </FormControl>
        <br />
        <br />
        <TextField
          label="Number of download codes"
          type="number"
          sx={{ width: "100%" }}
          onChange={(e) => setSelectedDownloadCodes(Number(e.target.value))}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={() => onCancel()}>Cancel</Button>
        <Button onClick={() => onConfirm()} autoFocus>
          Generate
        </Button>
      </DialogActions>
    </Dialog>
  );
};
export default GenerateDownloadCodesDialog;
