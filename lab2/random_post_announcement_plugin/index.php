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
        update_option('rpa_announcements', ($_POST['rpa_announcements']));
        echo '<div class="updated"><p>Announcements saved.</p></div>';
    }
    $announcements = get_option('rpa_announcements', "<p>Default Announcement</p>");
    ?>
    <div class="wrap">
        <h1>Random Post Announcements</h1>
        <form method="post">
            <textarea name="rpa_announcements" rows="10" cols="50" class="large-text"><?php echo esc_textarea($announcements); ?></textarea>
            <p><input type="submit" name="rpa_save_announcements" value="Save Announcements" class="button-primary"></p>
        </form>
    </div>
    <?php
 }

function rpa_add_announcement($content) {
    if (is_single()) {
        $announcements = get_option('rpa_announcements', "");
        $announcements_array = explode("\n", $announcements);
        $announcement = $announcements_array[array_rand($announcements_array)];
        $content = '<div class="rpa-announcement">' . wp_kses_post($announcement) . '</div>' . $content;
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