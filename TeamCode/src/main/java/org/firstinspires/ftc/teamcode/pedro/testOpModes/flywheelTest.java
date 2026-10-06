package org.firstinspires.ftc.teamcode.pedro.testOpModes;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedro.launchControl;

@TeleOp(name = "Flywheel Test")
public class flywheelTest extends LinearOpMode {
    launchControl launchControl = new launchControl();
    DcMotorEx launcher;
    Command spinTo;
    @Override
    public void runOpMode() throws InterruptedException {
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        launcher.setDirection(DcMotorSimple.Direction.FORWARD);
        double targetRPM = 4000;
        waitForStart();
        while (opModeIsActive()) {
            spinTo = launchControl.spinTo(targetRPM, launcher, 100);
            spinTo.execute();
            if (gamepad1.dpad_down) {
                targetRPM -= 100;
            } else if (gamepad1.dpad_up) {
                targetRPM += 100;
            }
            telemetry.addData("Power:",launcher.getPower());
            telemetry.addData("Target RPM", targetRPM);
            telemetry.addData("Current RPM", launcher.getVelocity(AngleUnit.DEGREES)/6);
            updateTelemetry(telemetry);
        }


    }
}
