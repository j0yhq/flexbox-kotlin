package com.j0y.flex

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Compose UI integration tests for [FlexBox].
 *
 * These tests verify that the [FlexBox] composable renders children at the
 * positions computed by [FlexboxEngine]. Tests use fixed-size Box children
 * and assert their positions/sizes using Compose's UI testing APIs.
 *
 * Test cases mirror the algorithm unit tests in [FlexboxEngineTest] but
 * operate end-to-end through the Compose rendering pipeline.
 */
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalTestApi::class)
class FlexBoxComposeTest {

    // ── justifyContent ────────────────────────────────────────────────────────

    @Test
    fun row_flexStart_childrenPackedLeft() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    justifyContent = JustifyContent.FlexStart,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(80.dp, 30.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(50.dp)
    }

    @Test
    fun row_flexEnd_childrenPackedRight() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    justifyContent = JustifyContent.FlexEnd,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(80.dp, 30.dp).testTag("b"))
            }
        }
        // free = 170, offset = 170
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo(170.dp)
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(220.dp)
    }

    @Test
    fun row_center_childrenCentered() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    justifyContent = JustifyContent.Center,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(80.dp, 30.dp).testTag("b"))
            }
        }
        // free=170, start=85
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo(85.dp)
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(135.dp)
    }

    @Test
    fun row_spaceBetween_threeChildren() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    justifyContent = JustifyContent.SpaceBetween,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(80.dp, 30.dp).testTag("b"))
                Box(Modifier.flexItem().size(60.dp, 30.dp).testTag("c"))
            }
        }
        // total=190, free=110, spacing=55
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(105.dp)
        onNodeWithTag("c").assertLeftPositionInRootIsEqualTo(240.dp)
    }

    @Test
    fun row_spaceEvenly_twoChildren() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    justifyContent = JustifyContent.SpaceEvenly,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("b"))
            }
        }
        // free=200, unit=200/3≈66.67
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo((200 / 3).dp)
    }

    // ── alignItems ────────────────────────────────────────────────────────────

    @Test
    fun alignItems_flexStart_childrenAtTop() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(200.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.FlexStart),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(50.dp, 50.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertTopPositionInRootIsEqualTo(0.dp)
    }

    @Test
    fun alignItems_flexEnd_childrenAtBottom() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(200.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.FlexEnd),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(50.dp, 50.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(70.dp)
        onNodeWithTag("b").assertTopPositionInRootIsEqualTo(50.dp)
    }

    @Test
    fun alignItems_center_childrenCenteredVertically() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(200.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.Center),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
            }
        }
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(35.dp)  // (100-30)/2
    }

    @Test
    fun alignItems_stretch_childrenFillHeight() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(200.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.Stretch),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(50.dp, 50.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertHeightIsEqualTo(100.dp)
        onNodeWithTag("b").assertHeightIsEqualTo(100.dp)
    }

    // ── flexWrap ──────────────────────────────────────────────────────────────

    @Test
    fun wrap_itemsFlowToNextLine() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(200.dp, 200.dp),
                containerStyle = FlexContainerStyle(
                    flexWrap = FlexWrap.Wrap,
                    alignItems = AlignItems.FlexStart,
                    alignContent = AlignContent.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(120.dp, 40.dp).testTag("a"))
                Box(Modifier.flexItem().size(120.dp, 60.dp).testTag("b"))
                Box(Modifier.flexItem().size(120.dp, 50.dp).testTag("c"))
            }
        }
        // Line1: a (120 fits); a+b=240>200 → b wraps to Line2; b+c=240>200 → c wraps to Line3
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertTopPositionInRootIsEqualTo(40.dp)
        onNodeWithTag("c").assertTopPositionInRootIsEqualTo(100.dp)
    }

    // ── flexGrow ──────────────────────────────────────────────────────────────

    @Test
    fun flexGrow_childExpandsToFillFreeSpace() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.FlexStart),
            ) {
                Box(
                    Modifier.flexItem(FlexItemStyle(flexGrow = 0f)).size(100.dp, 30.dp).testTag("a")
                )
                Box(Modifier.flexItem(FlexItemStyle(flexGrow = 1f)).size(0.dp, 30.dp).testTag("b"))
            }
        }
        onNodeWithTag("b").assertWidthIsEqualTo(200.dp)  // takes all free space
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(100.dp)
    }

    @Test
    fun flexGrow_equalGrowSplitsFreeSpace() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(200.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.FlexStart),
            ) {
                Box(Modifier.flexItem(FlexItemStyle(flexGrow = 1f)).size(0.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem(FlexItemStyle(flexGrow = 1f)).size(0.dp, 30.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertWidthIsEqualTo(100.dp)
        onNodeWithTag("b").assertWidthIsEqualTo(100.dp)
    }

    // ── column direction ──────────────────────────────────────────────────────

    @Test
    fun column_childrenStackVertically() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(100.dp, 300.dp),
                containerStyle = FlexContainerStyle(
                    flexDirection = FlexDirection.Column,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(60.dp, 50.dp).testTag("a"))
                Box(Modifier.flexItem().size(60.dp, 80.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertTopPositionInRootIsEqualTo(50.dp)
    }

    @Test
    fun column_spaceBetween() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(100.dp, 300.dp),
                containerStyle = FlexContainerStyle(
                    flexDirection = FlexDirection.Column,
                    justifyContent = JustifyContent.SpaceBetween,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(60.dp, 50.dp).testTag("a"))
                Box(Modifier.flexItem().size(60.dp, 80.dp).testTag("b"))
            }
        }
        // total=130, free=170 → b at 50+170=220
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertTopPositionInRootIsEqualTo(220.dp)
    }

    // ── alignSelf ─────────────────────────────────────────────────────────────

    @Test
    fun alignSelf_overridesContainerAlignItems() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(200.dp, 100.dp),
                containerStyle = FlexContainerStyle(alignItems = AlignItems.FlexStart),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(
                    Modifier.flexItem(FlexItemStyle(alignSelf = AlignSelf.FlexEnd))
                        .size(50.dp, 30.dp)
                        .testTag("b")
                )
            }
        }
        onNodeWithTag("a").assertTopPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertTopPositionInRootIsEqualTo(70.dp)  // 100-30
    }

    // ── gap ───────────────────────────────────────────────────────────────────

    @Test
    fun columnGap_addsSpaceBetweenRowItems() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    columnGap = 20f,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("b"))
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("c"))
            }
        }
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo(0.dp)
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(70.dp)   // 50+20
        onNodeWithTag("c").assertLeftPositionInRootIsEqualTo(140.dp)  // 70+50+20
    }

    // ── rowReverse ────────────────────────────────────────────────────────────

    @Test
    fun rowReverse_childrenOrderedFromRight() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    flexDirection = FlexDirection.RowReverse,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("a"))  // appears rightmost
                Box(Modifier.flexItem().size(80.dp, 30.dp).testTag("b"))
            }
        }
        onNodeWithTag("a").assertLeftPositionInRootIsEqualTo(250.dp)  // 300-50
        onNodeWithTag("b").assertLeftPositionInRootIsEqualTo(170.dp)  // 300-50-80
    }

    // ── nested FlexBox ────────────────────────────────────────────────────────

    @Test
    fun nestedFlexBox_childRendersCorrectly() = runComposeUiTest {
        setContent {
            FlexBox(
                modifier = Modifier.size(300.dp, 100.dp),
                containerStyle = FlexContainerStyle(
                    justifyContent = JustifyContent.SpaceBetween,
                    alignItems = AlignItems.FlexStart,
                ),
            ) {
                // Outer item contains a nested FlexBox
                FlexBox(
                    modifier = Modifier.flexItem().size(100.dp, 50.dp),
                    containerStyle = FlexContainerStyle(justifyContent = JustifyContent.Center),
                ) {
                    Box(Modifier.flexItem().size(20.dp, 20.dp).testTag("inner"))
                }
                Box(Modifier.flexItem().size(50.dp, 30.dp).testTag("outer"))
            }
        }
        onNodeWithTag("inner").assertLeftPositionInRootIsEqualTo(40.dp)   // (100-20)/2=40 from outer left
        onNodeWithTag("outer").assertLeftPositionInRootIsEqualTo(250.dp)  // SpaceBetween: 300-50
    }
}
