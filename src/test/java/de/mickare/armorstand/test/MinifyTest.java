package de.mickare.armorstand.test;

import static de.mickare.armortools.command.armorstand.MinifyClipboardCommand.convertMcRotationToClockRotation;
import static de.mickare.armortools.command.armorstand.MinifyClipboardCommand.rotate;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sk89q.worldedit.Vector;

public class MinifyTest {


  @Test
  public void testRotate() {
    Vector vec0 = new Vector(1, 0, 0);
    Vector vec1 = new Vector(0, 0, 1);
    Vector vec2 = new Vector(-1, 0, 0);
    Vector vec3 = new Vector(0, 0, -1);

    assertEquals(vec0, rotate(0, vec0));
    assertEquals(vec1, rotate(90, vec0));
    assertEquals(vec2, rotate(180, vec0));
    assertEquals(vec3, rotate(270, vec0));
    assertEquals(vec0, rotate(360, vec0));

    assertEquals(vec1, rotate(90, vec0));
    assertEquals(vec2, rotate(90, vec1));
    assertEquals(vec3, rotate(90, vec2));
    assertEquals(vec0, rotate(90, vec3));

    assertEquals(vec3, rotate(-90, vec0));
    assertEquals(vec0, rotate(-90, vec1));
    assertEquals(vec1, rotate(-90, vec2));
    assertEquals(vec2, rotate(-90, vec3));


    assertEquals(vec2, rotate(180, vec0));
    assertEquals(vec0, rotate(-180, vec2));

    assertEquals(vec3, rotate(180, vec1));
    assertEquals(vec1, rotate(-180, vec3));
  }

  @Test
  public void testRotateMc() {
    assertEquals(0, convertMcRotationToClockRotation(0));
    assertEquals(2, convertMcRotationToClockRotation(1));
    assertEquals(1, convertMcRotationToClockRotation(2));
    assertEquals(3, convertMcRotationToClockRotation(3));
  }

}
