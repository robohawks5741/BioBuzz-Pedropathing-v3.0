package org.firstinspires.ftc.teamcode.pedro.testOpModes;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class flywheelTunerTest {
    ElapsedTime stopwatch = new ElapsedTime();
    double lastRpm;
    double lastCheckSeconds;
    double currentRpm;
    double inertia;
    double high;
    double low;


    //Every loop, find deceleration, multiply by inertia to get torque, and multiply that by rad/s to get watts


    //other option, target speed, full power until at that speed, reduce power to 0.5, if slows down, increase, if speeds up, decrease.
    //do this slowly every other cycle. if at same power as last cycle and speed difference is very low, pin that as hold power.

}
