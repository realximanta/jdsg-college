package in.edu.jdsgcollege.bokakhat.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import in.edu.jdsgcollege.bokakhat.R
import in.edu.jdsgcollege.bokakhat.ui.theme.BluePrimary
import in.edu.jdsgcollege.bokakhat.ui.theme.TealAccent

@OptIn(ExperimentalFoundationApi::class) // <--- ADDED THIS
@Composable
fun LandingScreen(
    onMenuClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> SlideOneCollegeFront()
                1 -> SlideTwoPrincipal()
                2 -> SlideThreeCollegeDetails()
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = BluePrimary
                )
            }
            Row {
                TextButton(onClick = onSignUpClick) {
                    Text("Sign Up", color = BluePrimary)
                }
                TextButton(onClick = onLoginClick) {
                    Text("Login", color = BluePrimary)
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { iteration ->
                val color = if (pagerState.currentPage == iteration) TealAccent else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}

@Composable
fun SlideOneCollegeFront() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Local Image loaded from res/drawable/college_front.jpg
        Image(
            painter = painterResource(id = R.drawable.college_front),
            contentDescription = "College Front",
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "JDSG College",
            style = MaterialTheme.typography.headlineLarge,
            color = BluePrimary
        )
        Text(
            text = "Learn • Grow • Achieve",
            style = MaterialTheme.typography.titleLarge,
            color = TealAccent
        )
    }
}

@Composable
fun SlideTwoPrincipal() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, bottom = 80.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Local Image loaded from res/drawable/principal.jpg
        Image(
            painter = painterResource(id = R.drawable.principal),
            contentDescription = "Principal",
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Principal's Message",
            style = MaterialTheme.typography.headlineLarge,
            color = BluePrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "\"Education is the most powerful weapon which you can use to change the world. We strive to provide an environment of social justice and peace.\"",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SlideThreeCollegeDetails() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, bottom = 80.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "About the College",
            style = MaterialTheme.typography.headlineLarge,
            color = BluePrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Jogananda Deva Satradhikar Goswami (JDSG) College, established in 1964, is a premier educational institution located in Bokakhat, Golaghat, Assam. Affiliated with Dibrugarh University and recognized under the UGC Act, the college is dedicated to its vision of 'Education for Social Justice and Peace.'",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify
        )
    }
}
