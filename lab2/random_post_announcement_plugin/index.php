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
        $announcements = array_filter($_POST['rpa_announcements']);
        update_option('rpa_announcements', $announcements);
        echo '<div class="updated"><p>Announcements saved.</p></div>';
    }
    $announcements = get_option('rpa_announcements', []);
    ?>
    <div class="wrap">
        <h1>Random Post Announcements</h1>
        <form method="post">
            <div id="announcements-container">
                <?php foreach ($announcements as $announcement) : ?>
                    <div class="announcement-item">
                        <input type="text" name="rpa_announcements[]" value="<?php echo esc_attr($announcement); ?>" style="width:80%;">
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
            $announcement = $announcements[array_rand($announcements)];
            $content = '<div class="rpa-announcement">' . wp_kses_post($announcement) . '</div>' . $content;
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
?>