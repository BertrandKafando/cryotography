<script>
    // @ts-nocheck

    /**
     * @type {(MouseEvent & { currentTarget: EventTarget & Window; }) | null}
     */
    let mouseEvent = { clientX: 0, clientY: 0 };
</script>

<svelte:window on:mousemove={(e) => (mouseEvent = e)} />
<h1>About</h1>
<p>The mouse position is {mouseEvent.clientX}, {mouseEvent.clientY}.</p>
