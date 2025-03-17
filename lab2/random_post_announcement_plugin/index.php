<?php
/**
 * Plugin Name: Random Post Announcements
 * Description: This plugin displays random announcements on top of the post.
 * Version: 1.0
 * Author: Apolonia Abramowicz, Jakub Czajkowski
 */

 function rpa_admin_menu() {
    add_options_page("Random Post Announcements", "Random Post Announcements", "manage_options",
     "rpa_admin", "rpa_admin_page");
 }
 add_action('admin_menu', 'rpa_admin_menu');

 function rpa_admin_page() {
    if (isset($_POST['rpa_save_announcements'])) {
        $announcements = [];
        foreach ($_POST['rpa_announcements'] as $key => $announcement) {
            // Check if both announcement text and dates are provided
            $start_date = isset($_POST['rpa_start_date'][$key]) ? $_POST['rpa_start_date'][$key] : '';
            $end_date = isset($_POST['rpa_end_date'][$key]) ? $_POST['rpa_end_date'][$key] : '';
            if (!empty($announcement)) {
                // Save as an array of announcement data
                $announcements[] = [
                    'announcement' => $announcement,
                    'start_date' => $start_date,
                    'end_date' => $end_date
                ];
            }
        }
        update_option('rpa_announcements', $announcements);
        echo '<div class="updated"><p>Announcements saved.</p></div>';
    }

    $announcements = get_option('rpa_announcements', []);
    ?>
    <div class="wrap">
        <h1>Random Post Announcements</h1>
        <form method="post">
            <div id="announcements-container">
                <?php foreach ($announcements as $index => $announcement) : ?>
                    <div class="announcement-item">
                        <input type="text" name="rpa_announcements[]" value="<?php echo esc_attr($announcement['announcement']); ?>" style="width:60%;">
                        <input type="date" name="rpa_start_date[]" value="<?php echo esc_attr($announcement['start_date']); ?>">
                        <input type="date" name="rpa_end_date[]" value="<?php echo esc_attr($announcement['end_date']); ?>">
                        <button type="button" class="remove-announcement">Remove</button>
                    </div>
                <?php endforeach; ?>
            </div>
            <button type="button" id="add-announcement">Add Announcement</button>
            <p><input type="submit" name="rpa_save_announcements" value="Save Announcements" class="button-primary"></p>
        </form>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            document.getElementById('add-announcement').addEventListener('click', function() {
                let container = document.getElementById('announcements-container');
                let newInput = document.createElement('div');
                newInput.classList.add('announcement-item');
                newInput.innerHTML = '<input type="text" name="rpa_announcements[]" style="width:80%;" placeholder="New Announcement">' +
                                     '<input type="date" name="rpa_start_date[]" />' +
                                     '<input type="date" name="rpa_end_date[]" />' +
                                     '<button type="button" class="remove-announcement">Remove</button>';
                container.appendChild(newInput);
            });

            document.getElementById('announcements-container').addEventListener('click', function(e) {
                if (e.target.classList.contains('remove-announcement')) {
                    e.target.parentElement.remove();
                }
            });
        });
    </script>
    <?php
}

function rpa_add_announcement($content) {
    if (is_single()) {
        $announcements = get_option('rpa_announcements', []);
        if (!empty($announcements)) {
            $today = date('Y-m-d');
            $valid_announcements = [];

            // Filter announcements based on current date
            foreach ($announcements as $announcement) {
                if ((empty($announcement['start_date']) || $today >= $announcement['start_date']) &&
                    (empty($announcement['end_date']) || $today <= $announcement['end_date'])) {
                    $valid_announcements[] = $announcement['announcement'];
                }
            }

            // Display a random valid announcement if any
            if (!empty($valid_announcements)) {
                $announcement = $valid_announcements[array_rand($valid_announcements)];
                $content = '<div class="rpa-announcement">' . wp_kses_post($announcement) . '</div>' . $content;
            }
        }
    }
    return $content;
}
add_filter('the_content', 'rpa_add_announcement');
 
function rpa_register_styles() {
    wp_register_style('rpa-styles', plugins_url('/css/style.css', __FILE__));
    wp_enqueue_style('rpa-styles');
}
add_action('init', 'rpa_register_styles');

function rpa_shortcode() {
    $announcements = get_option('rpa_announcements', []);
    if(empty($announcements)){
        return '';
    }
    $today = date('Y-m-d');
    $valid_announcements = [];

    foreach ($announcements as $announcement) {
        if (
            (empty($announcement['start_date']) || $today >= $announcement['start_date']) &&
            (empty($announcement['end_date']) || $today <= $announcement['end_date'])
        )
        {
            $valid_announcements[] = $announcement['announcement'];
        }
    }

    if (!empty($valid_announcements)) {
        $random_announcement = $valid_announcements[array_rand($valid_announcements)];

        return '<div class="rpa-announcement">' . wp_kses_post($random_announcement) . '</div>';
    }

    return '';
}

add_shortcode('rpa_shortcode', 'rpa_shortcode')

?>