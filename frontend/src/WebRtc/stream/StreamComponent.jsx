import React, { Component, createRef } from "react";
import "./StreamComponent.css";
import OvVideoComponent from "./OvVideo";

import {
  MicOff,
  VideocamOff,
  VolumeUp,
  VolumeOff,
  HighlightOff,
  PanTool,
} from "@mui/icons-material";
import { FormControl, Input, InputLabel, IconButton, FormHelperText } from "@mui/material";

export default class StreamComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      nickname: this.props.user.getNickname(),
      showForm: false,
      mutedSound: false,
      isFormValid: true,
      isHandRaised: false,
      handRaiseStartTime: null,
      showSpeechText: false,
    };
    this.handleChange = this.handleChange.bind(this);
    this.handlePressKey = this.handlePressKey.bind(this);
    this.toggleNicknameForm = this.toggleNicknameForm.bind(this);
    this.toggleSound = this.toggleSound.bind(this);

    // MediaPipe 관련 Ref
    this.videoRef = createRef();
    this.canvasRef = createRef();
  }

  componentDidMount() {
    if (this.props.user.isLocal()) {
      console.log("나임");
      this.initializeMediaPipe();
      if (this.props.user && this.props.user.getStreamManager()) {
        this.props.user.getStreamManager().addVideoElement(this.videoRef.current);
      }
    }
  }

  initializeMediaPipe() {
    const videoElement = this.videoRef.current;
    const canvasElement = this.canvasRef.current;
    const canvasCtx = canvasElement.getContext("2d");

    const hands = new Hands({
      locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/hands/${file}`,
    });

    hands.setOptions({
      maxNumHands: 1,
      modelComplexity: 1,
      minDetectionConfidence: 0.5,
      minTrackingConfidence: 0.5,
    });

    hands.onResults((results) => {
      canvasCtx.save();
      canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);
      canvasCtx.drawImage(results.image, 0, 0, canvasElement.width, canvasElement.height);
      if (results.multiHandLandmarks && results.multiHandLandmarks.length > 0) {
        for (const landmarks of results.multiHandLandmarks) {
          drawConnectors(canvasCtx, landmarks, Hands.HAND_CONNECTIONS, {
            color: "#00FF00",
            lineWidth: 5,
          });
          drawLandmarks(canvasCtx, landmarks, { color: "#FF0000", lineWidth: 2 });

          // 손들기 제스처 인식
          if (this.isHandRaised(landmarks)) {
            if (!this.state.isHandRaised) {
              this.setState({ isHandRaised: true, handRaiseStartTime: new Date() });
            } else {
              const now = new Date();
              const duration = (now - this.state.handRaiseStartTime) / 1000;
              if (duration >= 2) {
                this.setState({ showSpeechText: true });
              }
            }
          } else {
            this.setState({ isHandRaised: false, handRaiseStartTime: null, showSpeechText: false });
          }
        }
      } else {
        this.setState({ isHandRaised: false, handRaiseStartTime: null, showSpeechText: false });
      }
      canvasCtx.restore();
    });

    const camera = new Camera(videoElement, {
      onFrame: async () => {
        await hands.send({ image: videoElement });
      },
      width: 640,
      height: 480,
    });

    camera.start();
  }

  isHandRaised(landmarks) {
    // 손들기 제스처 인식 로직
    // 여기서는 간단히 손목의 y좌표가 중지 끝의 y좌표보다 높으면 손든 것으로 인식
    const wrist = landmarks[0];
    const middleFingerTip = landmarks[12];
    return wrist.y > middleFingerTip.y;
  }

  handleChange(event) {
    this.setState({ nickname: event.target.value });
    event.preventDefault();
  }

  toggleNicknameForm() {
    if (this.props.user.isLocal()) {
      this.setState({ showForm: !this.state.showForm });
    }
  }

  toggleSound() {
    this.setState({ mutedSound: !this.state.mutedSound });
  }

  handlePressKey(event) {
    if (event.key === "Enter") {
      console.log(this.state.nickname);
      if (this.state.nickname.length >= 3 && this.state.nickname.length <= 20) {
        this.props.handleNickname(this.state.nickname);
        this.toggleNicknameForm();
        this.setState({ isFormValid: true });
      } else {
        this.setState({ isFormValid: false });
      }
    }
  }

  render() {
    return (
      <div className="OT_widget-container">
        <div className="pointer nickname">
          {this.state.showForm ? (
            <FormControl id="nicknameForm">
              <IconButton color="inherit" id="closeButton" onClick={this.toggleNicknameForm}>
                <HighlightOff />
              </IconButton>
              <InputLabel htmlFor="name-simple" id="label">
                Nickname
              </InputLabel>
              <Input
                color="inherit"
                id="input"
                value={this.state.nickname}
                onChange={this.handleChange}
                onKeyPress={this.handlePressKey}
                required
              />
              {!this.state.isFormValid && this.state.nickname.length <= 3 && (
                <FormHelperText id="name-error-text">Nickname is too short!</FormHelperText>
              )}
              {!this.state.isFormValid && this.state.nickname.length >= 20 && (
                <FormHelperText id="name-error-text">Nickname is too long!</FormHelperText>
              )}
            </FormControl>
          ) : (
            <div onClick={this.toggleNicknameForm}>
              <span id="nickname">{this.props.user.getNickname()}</span>
              {this.props.user.isLocal() && <span id=""> (edit)</span>}
            </div>
          )}
        </div>

        {this.props.user !== undefined && this.props.user.getStreamManager() !== undefined ? (
          <div className="streamComponent">
            {this.props.user.isLocal() ? (
              <>
                <video ref={this.videoRef} className="video" autoPlay={true} />
                <canvas ref={this.canvasRef} width="640" height="480" className="canvas" />
              </>
            ) : (
              <OvVideoComponent
                user={this.props.user}
                mutedSound={this.state.mutedSound}
                isHandRaised={this.state.isHandRaised}
              />
            )}

            <div id="statusIcons">
              {!this.props.user.isVideoActive() ? (
                <div id="camIcon">
                  <VideocamOff id="statusCam" />
                </div>
              ) : null}

              {!this.props.user.isAudioActive() ? (
                <div id="micIcon">
                  <MicOff id="statusMic" />
                </div>
              ) : null}

              {this.state.isHandRaised ? (
                <div id="handRaisedIcon">
                  <PanTool id="statusHandRaised" />
                </div>
              ) : null}
            </div>
            <div>
              {!this.props.user.isLocal() && (
                <IconButton id="volumeButton" onClick={this.toggleSound}>
                  {this.state.mutedSound ? <VolumeOff color="secondary" /> : <VolumeUp />}
                </IconButton>
              )}
            </div>
            {this.state.showSpeechText && (
              <div className="speechText">
                <span>발표 희망!</span>
              </div>
            )}
          </div>
        ) : null}
      </div>
    );
  }
}
