package org.firstinspires.ftc.teamcode.pedro.testOpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="car")
public class car extends LinearOpMode {
    DcMotorSimple rightDrive;
    DcMotorSimple leftDrive;
    Servo steer;
    @Override
    public void runOpMode() throws InterruptedException {
        rightDrive = hardwareMap.get(DcMotorSimple.class, "rDrive");
        leftDrive = hardwareMap.get(DcMotorSimple.class, "lDrive");
        steer = hardwareMap.get(Servo.class, "steer");
        leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        double lPow = 0.0;
        double rPow = 0.0;
        double steering = 0.0;
        steer.scaleRange(-0.9, 0.15);
        waitForStart();
        while (opModeIsActive()){
            lPow = gamepad1.left_stick_y*1;
            rPow = gamepad1.left_stick_y*1;
            steering = (1-gamepad1.right_stick_x)/2;
            rightDrive.setPower(rPow);
            leftDrive.setPower(lPow);
            steer.setPosition(steering);
            telemetry.addData("lPow", lPow);
            telemetry.addData("rPow", rPow);
            telemetry.addData("steer", steering);
            //telemetry.addData("steer", steer.getPosition());
            updateTelemetry(telemetry);
        }
    }
}
