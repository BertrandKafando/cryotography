<script>
    let count = 0;
    // keep variables in sync with each other using reactive declarations
    $: doubled = count * 2;

    // reactive statement
    $: if (count >= 10) {
        alert("count is dangerously high!");
        count = 9;
    }

    // update array and objects

    function increment() {
        count += 1;
    }
    function decrement() {
        count -= 1;
    }

    // others DOM events

    let m = {
        x: 0,
        y: 0,
    };

    function handleMousemove(event) {
        m.x = event.clientX;
        m.y = event.clientY;
    }
</script>

<div>
    <h1>Count: {count}</h1>

    <div class="buttons">
        <button on:click={increment}>Increment</button>
        <button on:click={decrement}>Decrement</button>
    </div>
    <h1>Doubled: {doubled}</h1>
    <div on:mousemove={handleMousemove}>
        <h1>Mouse position: {m.x}, {m.y}</h1>
    </div>
</div>

<style>
    .buttons {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
    }
</style>
