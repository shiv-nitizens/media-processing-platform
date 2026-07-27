import whisper

print("Loading Whisper model...")

model = whisper.load_model("base")

print("Whisper model loaded.")

def transcribe(audio_path: str):
    result = model.transcribe(audio_path)
    return result