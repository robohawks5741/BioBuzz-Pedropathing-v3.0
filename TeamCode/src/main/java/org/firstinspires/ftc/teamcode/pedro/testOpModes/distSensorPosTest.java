package org.firstinspires.ftc.teamcode.pedro.testOpModes;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name="distSensorPosTest", group = "Test")
public class distSensorPosTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DistanceSensor distSensorL = hardwareMap.get(DistanceSensor.class, "distSensorL");
        DistanceSensor distSensorR = hardwareMap.get(DistanceSensor.class, "distSensorR");
        Pose poseLeft;
        Pose poseRight;

        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("left", distSensorL.getDistance(DistanceUnit.INCH));
            telemetry.addData("right", distSensorR.getDistance(DistanceUnit.INCH));


            //assuming using left
            poseLeft = new Pose(8, (144-(distSensorL.getDistance(DistanceUnit.INCH))+8), 0);
            //assuming using right
            poseRight = new Pose(8, (distSensorR.getDistance(DistanceUnit.INCH)+8), 0);

            telemetry.addData("Pose (using Left)", poseLeft.toString());
            telemetry.addData("Pose (using Right)", poseRight.toString());
            updateTelemetry(telemetry);
        }
    }
}
