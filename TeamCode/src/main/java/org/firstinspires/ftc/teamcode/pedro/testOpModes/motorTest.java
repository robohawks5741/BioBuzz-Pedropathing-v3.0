package org.firstinspires.ftc.teamcode.pedro.testOpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="Motor Test")
public class motorTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorSimple motor = hardwareMap.get(DcMotorSimple.class, "testMotor");
        double commandPower = 0;
        waitForStart();
        while (opModeIsActive()){
            commandPower = gamepad1.left_stick_y;
            motor.setPower(commandPower);
            telemetry.addData("Command Power", commandPower);
            telemetry.addData("Motor Power", motor.getPower());
            updateTelemetry(telemetry);
        }
    }
}
