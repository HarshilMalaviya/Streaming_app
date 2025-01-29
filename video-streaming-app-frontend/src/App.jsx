import { useState } from "react";
import "./App.css";
import VideoUpload from "./components/VideoUpload";
import { Toaster } from "react-hot-toast";
import VideoPlayer from "./components/VideoPlayer";
import { TextInput, Button } from "flowbite-react"; // Importing both TextInput and Button

function App() {
  const [count, setCount] = useState(0);
  const [fieldValue, setFieldValue] = useState(null);
  const [videoId, setVideoId] = useState(53);

  function playVideo(videoId) {
    setVideoId(videoId);
  }

  return (
    <>
      <Toaster />
      <div className="flex flex-col  items-center space-y-9 justify-center py-9">
        <h1 className="text-2xl font-bold text-gray-700 dark:text-gray-100">
          Video Streaming App
        </h1>

        <div className="flex mt-14 w-full space-x-2  justify-between">
          <div className="w-full">
            <h1 className="text-white text-center mt-2">Playing Video</h1>

            <div>
              <VideoPlayer
                src={`http://localhost:3030/api/v1/videos/${videoId}/master.m3u8`}
              ></VideoPlayer>
            </div>
          </div>

          <div className="w-full">
            <VideoUpload />
          </div>
        </div>

        <div className="my-4 flex  space-x-4">
          <TextInput
            onChange={(event) => {
              setFieldValue(event.target.value);
            }}
            placeholder="Enter video id here"
            name="video_id_field"
          />
          <Button
            onClick={() => {
              if (fieldValue.trim() !== "") {
                setVideoId(Number(fieldValue)); // Convert input to a number
              }
            }}
          >
            Play
          </Button>
        </div>
      </div>
    </>
  );
}

export default App;
