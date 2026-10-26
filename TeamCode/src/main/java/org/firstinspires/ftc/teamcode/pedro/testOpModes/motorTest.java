package org.firstinspires.ftc.teamcode.pedro.testOpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name="Motor Test")
public class motorTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, "testMotor");
        DcMotorEx motor2 = hardwareMap.get(DcMotorEx.class, "testMotor2");
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor2.setDirection(DcMotorSimple.Direction.REVERSE);
        double commandPower = 0;
        waitForStart();
        while (opModeIsActive()){
            commandPower = gamepad1.left_stick_y;
            motor.setPower(commandPower);
            motor2.setPower(commandPower);
            //good launch velocity seems to be ~3000RPM
            telemetry.addData("Command Power", commandPower);
            telemetry.addData("Motor Power (1)", motor.getPower());
            telemetry.addData("Motor Velocity (1)", motor.getVelocity(AngleUnit.DEGREES)/6);
            telemetry.addData("Motor Velocity (2)", motor2.getVelocity()* (60.0/28.0));

            updateTelemetry(telemetry);
        }
    }
}
