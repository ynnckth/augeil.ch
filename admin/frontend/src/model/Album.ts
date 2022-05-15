import { DownloadCode } from "./DownloadCode";

export interface Album {
  id: string;
  artist: string;
  albumName: string;
  downloadCodes: DownloadCode[];
}
